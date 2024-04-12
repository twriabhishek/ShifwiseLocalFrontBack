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
  InputAdornment,
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
import { Visibility, VisibilityOff } from "@mui/icons-material";
import { ClipLoader } from "react-spinners";
import AlertPopUp from "./AlertPopUp";
import { Form } from "react-bootstrap";
import {
  API_BASE_URL,
  BUSINESS_UNIT,
  CREATE_USER,
  GET_ALL_ROLES,
  GROUP,
  PROCESS_UNIT,
  TEAMS,
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

function AddUser(props) {
  const {
    children,
    showModal,
    handleClose,
    setShowModal,
    handleUserCreated,
    ...other
  } = props;

  const [input, setInput] = useState({
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

  const [data, setData] = useState([]);
  const [created, setCreated] = useState(false);
  const [hasErrors, setHasErrors] = useState(false);
  const [businessUnitData, setBusinessUnitData] = useState([]);
  const [processUnitData, setProcessUnitData] = useState([]);
  const [teamData, setTeamData] = useState([]);
  const [groupData, setGroupData] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showPopup, setShowPopup] = useState(false);
  const businessUnit = localStorage.getItem("businessUnit");

  const handleChange = (event) => {
    const { name, value } = event.target;

    // Perform validation checks
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
    //
    setInput({ ...input, [name]: value });
  };

  const [showPassword, setShowPassword] = useState(false);
  const handleTogglePasswordVisibility = () => {
    setShowPassword((prevShowPassword) => !prevShowPassword);
  };

  const [selectRole, setSelectRole] = React.useState([]);
  const [roles, setRoles] = useState([]);

  const [isRoleSelected, setIsRoleSelected] = useState(false);
  const [userCreationCompleted, setUserCreationCompleted] = useState(false);

  const handleSubmitChange = (event) => {
    const { value } = event.target;
    console.log("form data is:", selectRole);
    let error = "";
    if (value.length < 1) {
      error = "Role is required";
    }
    setErrors({ ...errors, assignedRoles: error });

    setIsRoleSelected(event.target.value.length > 0);
    setInput({ ...input, assignedRoles: event.target.value });
  };
  const handleBusinessUnitChange = (event) => {
    const { value } = event.target;
    console.log("form data is:", selectRole);
    let error = "";
    if (value.length < 1) {
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

  const handleGroupChange = (event) => {
    const { value } = event.target;

    let error = "";
    if (value.length < 1) {
      error = "Group is required";
    }
    setErrors({ ...errors, group: error });

    setInput({ ...input, group: event.target.value });
  };

  const role = localStorage.getItem("roles");

  const header = {
    Authorization: localStorage.getItem("token"),
  };
  //Fetch user based on roles
  const fetchRoles = () => {
    const roleApi = `${API_BASE_URL}${GET_ALL_ROLES}`;
    axios
      .get(roleApi, { headers: header })
      .then((response) => {
        console.log("Fetch assignedRoles successful", response.data);
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
      .get(groupApi, { headers: header })
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

  const apiUrl = `${API_BASE_URL}${CREATE_USER}`;
  const handleSubmit = (event) => {
    if (!hasErrors) {
      setIsLoading(true);
      axios
        .post(
          apiUrl,
          {
            firstName: input.firstName,
            lastName: input.lastName,
            email: input.email,
            password: input.password,
            assignedRoles: input.assignedRoles,
            address: input.address,
            phonenumber: input.phonenumber,
            bussinessnumber: input.bussinessnumber,
            businessUnit: input.businessUnit,
            processUnit: input.processUnit,
            team: input.team,
            group: input.group,
          },
          { headers: header }
        )
        .then((response) => {
          setData([...data, response.data]);
          console.log("POST request successful", response.data);
          setIsLoading(true);
          handleUserCreated(response.data);
          message.success("User created successfully");
        })

        .catch((error) => {
          console.log("User creation failed", error.response);

          setTimeout(() => {
            message.error("User creation failed");
          }, 500);
          //
          if (error.response && error.response.status === 500) {
            message.error(
              "An error occurred while creating. Please try again later."
            );
          } else {
            message.error("Error creating user.");
          }
        })
        .finally(() => {
          setIsLoading(false);
          setUserCreationCompleted(true);
        });

      // CLEAR THE FORM AFTER SUBMISSION

      setData({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        assignedRoles: "",
        businessUnit: "",
        processUnit: "",
        team: "",
        group: "",
      });
      console.log("Data submitted:", input);
    }
  };

  //
  useEffect(() => {
    // Check businessUnit when the component mounts or when it changes
    if (businessUnit === null) {
      setShowPopup(true);
    }
  }, [businessUnit]);

  return (
    <Form onSubmit={handleSubmit}>
      <div>
        <Dialog maxWidth="xs" open={!showModal}>
          <DialogTitle display={"flex"} sx={{ m: 0, p: 2 }}>
            <Container>
              <div>Add user</div>
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
            {/* <Form onSubmit={handleSubmit}> */}
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth>
                  <TextField
                    id="outlined-basic"
                    label="Firstname"
                    name="firstName"
                    size="small"
                    variant="outlined"
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
                    size="small"
                    required
                    variant="outlined"
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
                    size="small"
                    required
                    variant="outlined"
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
                    label="Password"
                    name="password"
                    type={showPassword ? "text" : "password"}
                    required
                    variant="outlined"
                    onChange={handleChange}
                    error={!!errors.password}
                    helperText={errors.password}
                    InputProps={{
                      endAdornment: (
                        <InputAdornment position="end">
                          <IconButton
                            onClick={handleTogglePasswordVisibility}
                            edge="end"
                          >
                            {showPassword ? <Visibility /> : <VisibilityOff />}
                          </IconButton>
                        </InputAdornment>
                      ),
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
                <FormControl>
                  <TextField
                    id="outlined-basic"
                    label="Address"
                    name="address"
                    size="small"
                    required
                    variant="outlined"
                    onChange={handleChange}
                    error={!!errors.address}
                    helperText={errors.address}
                  />
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl>
                  <TextField
                    id="outlined-basic"
                    label="Phonenumber"
                    name="phonenumber"
                    size="small"
                    required
                    variant="outlined"
                    onChange={handleChange}
                    error={!!errors.phonenumber}
                    helperText={errors.phonenumber}
                  />
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl>
                  <TextField
                    id="outlined-basic"
                    label="Businessnumber"
                    name="bussinessnumber"
                    size="small"
                    required
                    variant="outlined"
                    onChange={handleChange}
                    error={!!errors.bussinessnumber}
                    helperText={errors.bussinessnumber}
                  />
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth variant="outlined">
                  <InputLabel
                    id="demo-simple-select-outlined-label"
                    error={!!errors.businessUnit}
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
                  >
                    {businessUnitData.map((name) => (
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
                  >
                    {processUnitData.map((name) => (
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
                    label="Status"
                  >
                    {teamData.map((name) => (
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
                    label="Status"
                  >
                    {groupData.map((name) => (
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
            </Grid>
            {/* </Form> */}
          </DialogContent>
          <Container>
            <DialogActions sx={{ margin: "5px" , justifyContent: "flex-start"}}>
              <Button
                type="submit"
                variant="contained"
                color="success"
                id="UserModuleBtn"
                autoFocus
                onClick={() => {
                  handleSubmit();
                  setCreated(true);
                  handleClose();
                }}
                disabled={
                  !Object.values(input).some((field) => field === "") ||
                  !input.firstName ||
                  !input.lastName ||
                  !input.email ||
                  !input.password ||
                  !input.address ||
                  !input.phonenumber ||
                  !input.bussinessnumber ||
                  input.assignedRoles.length === 0 ||
                  input.businessUnit.length === 0 ||
                  input.team.length === 0 ||
                  input.group.length === 0 ||
                  Object.values(errors).some((error) => !!error)
                }
              >
                Create
                <div style={{ display: "flex", alignItems: "center" }}>
                  {isLoading && (
                    <ClipLoader color="#36D7B7" loading={isLoading} size={20} />
                  )}
                </div>
              </Button>
            </DialogActions>
          </Container>
        </Dialog>

        <AlertPopUp showPopup={showPopup} />
      </div>
    </Form>
  );
}

export default AddUser;
