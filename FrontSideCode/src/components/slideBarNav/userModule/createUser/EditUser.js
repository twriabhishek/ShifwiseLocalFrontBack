import {
  Button,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  Grid,
  IconButton,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
  TextField,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import CloseIcon from "@mui/icons-material/Close";
import axios from "axios";
import { message } from "antd";
import { useNavigate, useParams } from "react-router-dom";
import {
  API_BASE_URL,
  BUSINESS_UNIT,
  GET_ALL_ROLES,
  GROUP,
  PROCESS_UNIT,
  TEAMS,
  UPDATE_USER,
} from "../../../constant-API/constants";

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

function EditUser(props) {
  const { id } = useParams();
  const {
    title,

    children,
    showEditModel,
    closeDialog,
    handleClose,
    setShowEditModel,
    handleUserCreated,
    handleUserUpdated,
    userData,

    ...other
  } = props;

  const [input, setInput] = useState({
    id: "id",
    clientId: "",
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    assignedRoles: [],
    address: "",
    phonenumber: "",
    bussinessnumber: "",
    businessUnit: "",
    processUnit: "",
    team: "",
    group: "",
    active: "",
  });
  const [errors, setErrors] = useState({
    id: "id",
    clientId: "",
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    assignedRoles: "",
    address: "",
    phonenumber: "",
    bussinessnumber: "",
    businessUnit: "",
    processUnit: "",
    team: "",
    group: "",
    active: "",
  });

  const [businessUnitData, setBusinessUnitData] = useState([]);
  const [processUnitData, setProcessUnitData] = useState([]);
  const [teamData, setTeamData] = useState([]);
  const [groupData, setGroupData] = useState([]);

  const handleChange = (event) => {
    const { name, value } = event.target;

    //validations
    let error = "";
    if (name === "firstName" && value.trim() === "") {
      error = "Firstname is required";
    } else if (
      name === "firstName" &&
      !/^(?=.*[a-zA-Z])[a-zA-Z0-9]+$/.test(value)
    ) {
      error = "Invalid Firstname";
    }
    if (name === "lastName" && value.trim() === "") {
      error = "Lastname is required";
    } else if (
      name === "lastName" &&
      !/^(?=.*[a-zA-Z])[a-zA-Z0-9]+$/.test(value)
    ) {
      error = "Invalid Lastname";
    }
    if (name === "email" && value.trim() === "") {
      error = "Email is required";
    } else if (
      name === "email" &&
      !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.(com|in|ai)$/i.test(value)
    ) {
      error = "Invalid email";
    }
    if (name === "password" && value.trim() === "") {
      error = "Password is required";
    } else if (
      name === "password" &&
      !/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,}$/.test(value)
    ) {
      error =
        "Password must be at least 8 characters and contain at least one uppercase letter and one number";
    }
    if (name === "address" && value.trim() === "") {
      error = "Address is required";
    } else if (name === "address" && !/^[a-zA-Z0-9\s,.'-]{3,}$/.test(value)) {
      error = "Invalid address";
    }
    if (name === "phonenumber" && value.trim() === "") {
      error = "Phonenumber is required";
    } else if (
      name === "phonenumber" &&
      !/^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/.test(
        value
      )
    ) {
      error = "Invalid phonenumber";
    }
    if (name === "bussinessnumber" && value.trim() === "") {
      error = "Businessnumber is required";
    } else if (
      name === "bussinessnumber" &&
      !/^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/.test(
        value
      )
    ) {
      error = "Invalid phonenumber";
    }
    if (name === "active" && value === "") {
      error = "Status is required";
    }

    setErrors({
      ...errors,
      [name]: error,
    });

    setInput({ ...input, [event.target.name]: event.target.value });
  };

  const [selectRole, setSelectRole] = React.useState([]);
  const [roles, setRoles] = useState([]);
  const [data, setData] = useState([]);
  const [isRoleSelected, setIsRoleSelected] = useState(false);
  const [hasErrors, setHasErrors] = useState(false);
  const role = localStorage.getItem("roles");

  const handleSubmitChange = (event) => {
    const { value } = event.target;
    console.log("form data is:", selectRole);
    let error = "";
    const selectedRoles = Array.isArray(value) ? value : [value];
    if (selectedRoles.length === 0) {
      error = "Role is required";
    }
    setErrors({ ...errors, assignedRoles: error });

    setIsRoleSelected(selectedRoles.length > 0);
    setInput({ ...input, assignedRoles: selectedRoles });
  };
  //
  const handleBusinessUnitChange = (event) => {
    const { value } = event.target;
    console.log("form data is:", selectRole);
    let error = "";
    if (value && value.length < 1) {
      error = "Businessunit is required";
    }
    setErrors({ ...errors, businessUnit: error });

    setInput({ ...input, businessUnit: event.target.value });
  };
  //
  const handleProcessUnitChange = (event) => {
    const { value } = event.target;
    console.log("form data is:", selectRole);
    let error = "";
    if (value.length < 1) {
      error = "Processunit is required";
    }
    setErrors({ ...errors, processUnit: error });

    setInput({ ...input, processUnit: event.target.value });
  };
  //
  const handleTeamChange = (event) => {
    const { value } = event.target;

    let error = "";
    if (value.length < 1) {
      error = "Team is required";
    }
    setErrors({ ...errors, team: error });

    setInput({ ...input, team: event.target.value });
  };
  //
  const handleGroupChange = (event) => {
    const { value } = event.target;

    let error = "";
    if (value.length < 1) {
      error = "Group is required";
    }
    setErrors({ ...errors, group: error });

    setInput({ ...input, group: event.target.value });
  };
  //
  //Fetch business unit
  const fetchBusinessunit = () => {
    const businessUnitApi = `${API_BASE_URL}${BUSINESS_UNIT}`;
    axios
      .get(businessUnitApi, { headers: header })
      .then((response) => {
        console.log("Fetch businessUnit successful", response.data);
        setBusinessUnitData(response.data);
      })
      .catch((err) => {
        console.log("fetch business unit failed!", err);
      });
  };
  useEffect(() => {
    fetchBusinessunit();
  }, []);

  //Fetch Process unit
  const fetchProcessunit = () => {
    const processUnitApi = `${API_BASE_URL}${PROCESS_UNIT}`;
    axios
      .get(processUnitApi, { headers: header })
      .then((response) => {
        console.log("Fetch ProcessUnit successful", response.data);
        setProcessUnitData(response.data);
      })
      .catch((err) => {
        console.log("fetch Processunit failed!", err);
      });
  };
  useEffect(() => {
    fetchProcessunit();
  }, []);

  //Fetch teams
  const fetchTeams = () => {
    const teamsApi = `${API_BASE_URL}${TEAMS}`;
    axios
      .get(teamsApi, { headers: header })
      .then((response) => {
        console.log("Fetch teams successful", response.data);
        setTeamData(response.data);
      })
      .catch((err) => {
        console.log("fetch teams failed!", err);
      });
  };
  useEffect(() => {
    fetchTeams();
  }, []);

  // Fetch group
  const fetchGroup = () => {
    const groupApi = `${API_BASE_URL}${GROUP}`;
    axios
      .get(
        groupApi,

        { headers: header }
      )
      .then((response) => {
        console.log("Fetch group successful", response.data);
        setGroupData(response.data);
      })
      .catch((err) => {
        console.log("fetch group failed!", err);
      });
  };
  useEffect(() => {
    fetchGroup();
  }, []);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  //fetch roles
  const fetchRoles = () => {
    const roleApi = `${API_BASE_URL}${GET_ALL_ROLES}`;
    axios
      .get(roleApi, { headers: header })
      .then((response) => {
        console.log("GET role by role id", response.data);
        if (role !== "SUPERADMIN") {
          // Filter out the SUPERADMIN role from the roles array
          const filteredRoles = response.data.filter(
            (role) => role.name !== "SUPERADMIN"
          );
          setRoles(filteredRoles);
        } else {
          setRoles(response.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  useEffect(() => {
    fetchRoles();
  }, []);

  useEffect(() => {
    if (userData) {
      console.log("user data", userData);
      setInput({
        ...input,
        id: userData.id,
        clientId: userData.clientId,
        firstName: userData.firstName,
        lastName: userData.lastName,
        email: userData.email,
        password: userData.password,
        assignedRoles: userData.assignedRoles,
        address: userData.address,
        phonenumber: userData.phonenumber,
        bussinessnumber: userData.bussinessnumber,
        businessUnit: userData.businessUnit,
        processUnit: userData.processUnit,
        team: userData.team,
        group: userData.group,
        active: userData.active ? "true" : "false",
      });
    }
  }, [userData]);

  const handleSubmit = (event) => {
    const apiUrl = `${API_BASE_URL}${UPDATE_USER}/${userData.id}`;
    if (!hasErrors) {
      axios
        .put(apiUrl, input, { headers: header })
        .then((response) => {
          setData(response.data);
          handleUserUpdated(response.data);
          console.log("PUT request successful", response.data);
          setTimeout(() => {
            message.success("Data updated successfully");
          }, 1000);

          setShowEditModel(false);
        })
        .catch((error) => {
          console.log(error);
        });
    }
  };

  return (
    <div>
      <Dialog maxWidth="xs" open={showEditModel}>
        <DialogTitle display={"flex"} sx={{ m: 0, p: 2 }}>
          <Container>
            <div>Edit user</div>
            <span>
              <IconButton
                onClick={handleClose}
                sx={{
                  position: "absolute",
                  right: 8,
                  top: 8,
                  color: (theme) => theme.palette.grey[500],
                }}
              >
                <CloseIcon />
              </IconButton>
            </span>
          </Container>
        </DialogTitle>
        <DialogContent dividers>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <TextField
                  id="outlined-basic"
                  label="Firstname"
                  name="firstName"
                  variant="outlined"
                  size="small"
                  value={input.firstName}
                  onChange={handleChange}
                  error={!!errors.firstName}
                  helperText={errors.firstName}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <TextField
                  id="outlined-basic"
                  label="Lastname"
                  name="lastName"
                  value={input.lastName}
                  required
                  variant="outlined"
                  size="small"
                  onChange={handleChange}
                  error={!!errors.lastName}
                  helperText={errors.lastName}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl>
                <TextField
                  id="outlined-basic"
                  label="Email"
                  name="email"
                  value={input.email}
                  required
                  variant="outlined"
                  size="small"
                  onChange={handleChange}
                  error={!!errors.email}
                  helperText={errors.email}
                />
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <TextField
                  id="outlined-basic"
                  label="Address"
                  name="address"
                  variant="outlined"
                  size="small"
                  value={input.address}
                  onChange={handleChange}
                  error={!!errors.address}
                  helperText={errors.address}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <TextField
                  id="outlined-basic"
                  label="Phonenumber"
                  name="phonenumber"
                  variant="outlined"
                  size="small"
                  value={input.phonenumber}
                  onChange={handleChange}
                  error={!!errors.phonenumber}
                  helperText={errors.phonenumber}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <TextField
                  id="outlined-basic"
                  label="Businessnumber"
                  name="bussinessnumber"
                  variant="outlined"
                  size="small"
                  value={input.bussinessnumber}
                  onChange={handleChange}
                  error={!!errors.bussinessnumber}
                  helperText={errors.bussinessnumber}
                  InputLabelProps={{
                    shrink: input.bussinessnumber !== "",
                  }}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel
                  id="demo-multiple-name-label"
                  error={!!errors.assignedRoles}
                >
                  Roles
                </InputLabel>
                <Select
                  labelId="demo-multiple-name-label"
                  id="demo-multiple-name"
                  multiple
                  value={input.assignedRoles}
                  onChange={handleSubmitChange}
                  error={!!errors.assignedRoles}
                  input={<OutlinedInput label="Name" />}
                  MenuProps={MenuProps}
                >
                  {roles.map((name) => (
                    <MenuItem key={name.id} value={name.id}>
                      {/* {name.id} */}
                      {name.name}
                    </MenuItem>
                  ))}
                </Select>

                {errors.assignedRoles && (
                  <div style={{ color: "#ff0000", fontSize: "12px" }}>
                    {errors.assignedRoles}
                  </div>
                )}
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <FormControl fullWidth variant="outlined">
                <InputLabel
                  id="demo-simple-select-outlined-label"
                  error={!!errors.businessUnit}
                  shrink={input.businessUnit !== ""}
                  sx={{ marginBottom: "8px" }}
                >
                  Businessunit
                </InputLabel>
                <Select
                  labelId="demo-simple-select-outlined-label"
                  id="demo-simple-select-outlined"
                  name="businessUnit"
                  value={input.businessUnit}
                  onChange={handleBusinessUnitChange}
                  error={!!errors.businessUnit}
                  // helperText={errors.active}
                  label="Status"
                  InputLabelProps={{
                    shrink: input.businessUnit !== "",
                  }}
                >
                  {businessUnitData?.map((name) => (
                    <MenuItem
                      key={name.businessUnitId}
                      value={name.businessUnitName}
                    >
                      {name.businessUnitName}
                    </MenuItem>
                  ))}
                </Select>
                {errors.businessUnit && (
                  <div style={{ color: "#ff0000", fontSize: "12px" }}>
                    {errors.businessUnit}
                  </div>
                )}
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth variant="outlined">
                <InputLabel
                  id="demo-simple-select-outlined-label"
                  error={!!errors.processUnit}
                  shrink={input.processUnit !== ""}
                  sx={{ marginBottom: "8px" }}
                >
                  Processunit
                </InputLabel>
                <Select
                  labelId="demo-simple-select-outlined-label"
                  id="demo-simple-select-outlined"
                  name="processUnit"
                  value={input.processUnit}
                  onChange={handleProcessUnitChange}
                  error={!!errors.processUnit}
                  // helperText={errors.active}
                  label="Status"
                  InputLabelProps={{
                    shrink: input.processUnit !== "",
                  }}
                >
                  {processUnitData?.map((name) => (
                    <MenuItem
                      key={name.processUnitId}
                      value={name.processUnitName}
                    >
                      {name.processUnitName}
                    </MenuItem>
                  ))}
                </Select>
                {errors.processUnit && (
                  <div style={{ color: "#ff0000", fontSize: "12px" }}>
                    {errors.processUnit}
                  </div>
                )}
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth variant="outlined">
                <InputLabel
                  id="demo-simple-select-outlined-label"
                  error={!!errors.team}
                  shrink={input.team !== ""}
                  sx={{ marginBottom: "8px" }}
                >
                  Team
                </InputLabel>
                <Select
                  labelId="demo-simple-select-outlined-label"
                  id="demo-simple-select-outlined"
                  name="team"
                  value={input.team}
                  onChange={handleTeamChange}
                  error={!!errors.team}
                  // helperText={errors.active}
                  label="Status"
                  InputLabelProps={{
                    shrink: input.team !== "",
                  }}
                >
                  {teamData?.map((name) => (
                    <MenuItem key={name.teamId} value={name.teamName}>
                      {name.teamName}
                    </MenuItem>
                  ))}
                </Select>
                {errors.team && (
                  <div style={{ color: "#ff0000", fontSize: "12px" }}>
                    {errors.team}
                  </div>
                )}
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth variant="outlined">
                <InputLabel
                  id="demo-simple-select-outlined-label"
                  error={!!errors.group}
                  shrink={input.group !== ""}
                  sx={{ marginBottom: "8px" }}
                >
                  Group
                </InputLabel>
                <Select
                  labelId="demo-simple-select-outlined-label"
                  id="demo-simple-select-outlined"
                  name="team"
                  value={input.group}
                  onChange={handleGroupChange}
                  error={!!errors.group}
                  // helperText={errors.active}
                  label="Status"
                  InputLabelProps={{
                    shrink: input.group !== "",
                  }}
                >
                  {groupData?.map((name) => (
                    <MenuItem key={name.groupId} value={name.groupName}>
                      {name.groupName}
                    </MenuItem>
                  ))}
                </Select>
                {errors.group && (
                  <div style={{ color: "#ff0000", fontSize: "12px" }}>
                    {errors.group}
                  </div>
                )}
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth variant="outlined">
                <InputLabel
                  id="demo-simple-select-outlined-label"
                  error={!!errors.active}
                >
                  Status
                </InputLabel>
                <Select
                  labelId="demo-simple-select-outlined-label"
                  id="demo-simple-select-outlined"
                  name="active"
                  value={input.active}
                  onChange={handleChange}
                  error={!!errors.active}
                  // helperText={errors.active}
                  label="Status"
                >
                  <MenuItem value="">
                    <em>None</em>
                  </MenuItem>
                  <MenuItem value="true">Active</MenuItem>
                  <MenuItem value="false">Inactive</MenuItem>
                </Select>
                {errors.active && (
                  <div style={{ color: "#ff0000", fontSize: "12px" }}>
                    {errors.active}
                  </div>
                )}
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <Container>
          <DialogActions sx={{ margin: "5px" }}>
            <Button
              type="submit"
              variant="contained"
              color="success"
              id="UserModuleBtn"
              autoFocus
              onClick={() => {
                handleSubmit();
                handleClose();
              }}
            >
              Update
            </Button>
          </DialogActions>
        </Container>
      </Dialog>
    </div>
  );
}

export default EditUser;
