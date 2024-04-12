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
import {
  API_BASE_URL,
  GET_ALL_ROLES,
  UPDATE_CLIENT,
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
function EditClient(props) {
  const {
    title,

    children,
    showEditClientModel,
    closeDialog,
    handleClose,
    setShowEditClientModel,
    handleUserCreated,
    userData,
    clientData,
    handleClientUpdate,

    ...other
  } = props;
  const [clientInput, setClientInput] = useState({
    clientId: "",
    clientName: "",
    spocName: "",
    email: "",
    password: "",
    address: "",
    phonenumber: "",
    bussinessnumber: "",
    assignedRoles: [],
    active: "",
  });
  const [errors, setErrors] = useState({
    clientId: "",
    clientName: "",
    spocName: "",
    email: "",
    password: "",
    address: "",
    phonenumber: "",
    bussinessnumber: "",
    assignedRoles: "",
    active: "",
  });
  const [roles, setRoles] = useState([]);
  const [updatedClientData, setUpdatedClientData] = useState([]);
  const [isRoleSelected, setIsRoleSelected] = useState(false);
  const [hasErrors, setHasErrors] = useState(false);
  const handleChange = (event) => {
    const { name, value } = event.target;
    //validation
    let error = "";
    if (name === "clientName" && value.trim() === "") {
      error = "Name is required";
    } else if (
      name === "clientName" &&
      !/^(?=.*[a-zA-Z])[a-zA-Z0-9]+$/.test(value)
    ) {
      error = "Invalid Name";
    }
    if (name === "spocName" && value.trim() === "") {
      error = "Name is required";
    } else if (
      name === "spocName" &&
      !/^(?=.*[a-zA-Z])[a-zA-Z0-9]+$/.test(value)
    ) {
      error = "Invalid Name";
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
    setClientInput({ ...clientInput, [event.target.name]: event.target.value });
  };
  const handleSubmitChange = (event) => {
    const { name, value } = event.target;
    let error = "";
    const selectedRoles = Array.isArray(value) ? value : [value];
    if (selectedRoles.length === 0) {
      error = "Role is required";
    }
    setErrors({ ...errors, assignedRoles: error });

    setIsRoleSelected(selectedRoles.length > 0);
    setClientInput({ ...clientInput, assignedRoles: selectedRoles });
  };

  useEffect(() => {
    if (clientData) {
      console.log("Client data", clientData);
      setClientInput({
        ...clientInput,
        clientId: clientData.clientId,
        clientName: clientData.clientName,
        spocName: clientData.spocName,
        email: clientData.email,
        password: clientData.password,
        phonenumber: clientData.phonenumber,
        bussinessnumber: clientData.bussinessnumber,
        address: clientData.address,
        assignedRoles: clientData.assignedRoles,
        active: clientData.active ? "true" : "false",
      });
    }
  }, [clientData]);
  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchRoles = () => {
    const getAllRoleApi = `${API_BASE_URL}${GET_ALL_ROLES}`;
    axios
      .get(getAllRoleApi, { headers: header })
      .then((response) => {
        console.log("GET role by role id", response.data);
        setRoles(response.data);
      })
      .catch((err) => {
        console.log(err);
      });
  };
  useEffect(() => {
    fetchRoles();
  }, []);

  const handleSubmit = (event) => {
    const updateClientApi = `${API_BASE_URL}${UPDATE_CLIENT}/${clientData.clientId}`;
    if (!hasErrors) {
      axios
        .put(updateClientApi, clientInput, { headers: header })
        .then((response) => {
          setUpdatedClientData(response.data);
          handleClientUpdate(response.data);
          console.log("PUT request successful", response.data);

          setTimeout(() => {
            message.success("Client updated successfully");
          }, 500);
        })
        .catch((error) => {
          console.log(error);
        });
    }
  };

  return (
    <div>
      <Dialog maxWidth="xs" open={showEditClientModel}>
        <DialogTitle display={"flex"} sx={{ m: 0, p: 1 }}>
          <Container>
            <div>Edit client</div>
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
                  label="ClientName"
                  name="clientName"
                  variant="outlined"
                  size="small"
                  required
                  value={clientInput.clientName}
                  onChange={handleChange}
                  error={!!errors.clientName}
                  helperText={errors.clientName}
                  InputLabelProps={{
                    shrink: clientInput.clientName !== "",
                  }}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <TextField
                  id="outlined-basic"
                  label="Name"
                  name="spocName"
                  variant="outlined"
                  size="small"
                  required
                  value={clientInput.spocName}
                  onChange={handleChange}
                  error={!!errors.spocName}
                  helperText={errors.spocName}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <TextField
                  id="outlined-basic"
                  label="Email"
                  name="email"
                  required
                  size="small"
                  value={clientInput.email}
                  variant="outlined"
                  onChange={handleChange}
                  error={!!errors.email}
                  helperText={errors.email}
                  InputLabelProps={{
                    shrink: clientInput.email !== "",
                  }}
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
                  required
                  value={clientInput.address}
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
                  required
                  value={clientInput.phonenumber}
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
                  label="Business number"
                  name="bussinessnumber"
                  variant="outlined"
                  size="small"
                  required
                  value={clientInput.bussinessnumber}
                  onChange={handleChange}
                  error={!!errors.bussinessnumber}
                  helperText={errors.bussinessnumber}
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
                  name="assignedRoles"
                  variant="outlined"
                  required
                  multiple
                  value={clientInput.assignedRoles}
                  onChange={handleSubmitChange}
                  error={!!errors.assignedRoles}
                  // helperText={errors.firstName}
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
                  error={!!errors.active}
                >
                  Status
                </InputLabel>
                <Select
                  labelId="demo-simple-select-outlined-label"
                  id="demo-simple-select-outlined"
                  name="active"
                  required
                  value={clientInput.active}
                  onChange={handleChange}
                  error={!!errors.active}
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
              id="UserModuleBtn"
              autoFocus
              onClick={() => {
                handleSubmit();
                handleClose();
              }}
              disabled={
                Object.values(clientInput).some((field) => field === "") ||
                clientInput.assignedRoles.length === 0 ||
                Object.values(errors).some((error) => !!error) ||
                clientInput.active === ""
              }
            >
              Update
            </Button>
          </DialogActions>
        </Container>
      </Dialog>
    </div>
  );
}

export default EditClient;
