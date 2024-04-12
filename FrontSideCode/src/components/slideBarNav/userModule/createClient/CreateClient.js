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
import {
  API_BASE_URL,
  CREATE_CLIENT,
  GET_ALL_ROLES,
} from "../../../constant-API/constants";
import PropTypes from "prop-types";

function CreateClient(props) {
  const {
    children,
    showClientModal,
    handleClose,
    setShowClientModal,
    handleClientCreated,

    ...other
  } = props;

  CreateClient.propTypes = {
    children: PropTypes.node,
    showClientModal: PropTypes.bool,
    handleClose: PropTypes.func,
    setShowClientModal: PropTypes.func,
    handleClientCreated: PropTypes.func,
  };
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
    assignedRoleName: [],
    businessUnit: "",
    processUnit: "",
    team: "",
    group: "",
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
    assignedRoleName: "",
  });
  const [clientRole, setClientRole] = useState([]);
  const [data, setData] = useState([]);
  const [isRoleSelected, setIsRoleSelected] = useState(false);
  const [hasErrors, setHasErrors] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const role = localStorage.getItem("roles");

  const isFieldEmpty = (value) => value.trim() === "";
  const isValidName = (value) => /^(?=.*[a-zA-Z])[a-zA-Z0-9]+$/.test(value);
  const isValidSpocName = (value) => /^(?=.*[a-zA-Z])[a-zA-Z0-9]+$/.test(value);
  const isValidEmail = (value) =>
    /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.(com|in|ai)$/i.test(value);
  const isValidPassword = (value) =>
    /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,}$/.test(value);
  const isValidAddress = (value) => /^[a-zA-Z0-9\s,.'-]{3,}$/.test(value);
  const isValidPhoneNumber = (value) =>
    /^(\d{3})?[-\s.]?\d{3}[-\s.]?\d{4}$/.test(value);
  const isFieldRequired = (value) => value === "";

  const handleChange = (event) => {
    const { name, value } = event.target;

    let error = "";

    if (name === "clientName") {
      if (isFieldEmpty(value)) {
        error = "Name is required";
      } else if (!isValidName(value)) {
        error = "Invalid Name";
      }
    } else if (name === "spocName") {
      console.log(name, value);
      if (isFieldEmpty(value)) {
        error = "Spoc name is required";
      } else if (!isValidSpocName(value)) {
        error = "Invalid spocname";
      }
    } else if (name === "email") {
      if (isFieldEmpty(value)) {
        error = "Email is required";
      } else if (!isValidEmail(value)) {
        error = "Invalid email";
      }
    } else if (name === "password") {
      if (isFieldEmpty(value)) {
        error = "Password is required";
      } else if (!isValidPassword(value)) {
        error =
          "Password must be at least 8 characters and contain at least one uppercase letter and one number";
      }
    } else if (name === "address") {
      if (isFieldEmpty(value)) {
        error = "Address is required";
      } else if (!isValidAddress(value)) {
        error = "Invalid address";
      }
    } else if (name === "phonenumber" || name === "bussinessnumber") {
      if (isFieldEmpty(value)) {
        error = "Phonenumber is required";
      } else if (!isValidPhoneNumber(value)) {
        error = "Invalid phonenumber";
      }
    } else if (name === "active") {
      if (isFieldRequired(value)) {
        error = "Status is required";
      }
    }

    setErrors({
      ...errors,
      [name]: error,
    });
    setClientInput({ ...clientInput, [name]: value });
  };

  const handleSubmitChange = (event) => {
    console.log(event.target.name, event.target.value.length);
    const { value } = event.target;
    //validations
    let error = "";
    if (value.length < 1) {
      error = "Role is required";
    }
    setErrors({ ...errors, assignedRoles: error });

    setIsRoleSelected(event.target.value.length > 0);

    setClientInput({ ...clientInput, assignedRoles: event.target.value });
  };

  const ITEM_HEIGHT = 48;
  const ITEM_PADDING_TOP = 8;
  const MenuProps = {
    PaperProps: {
      style: {
        maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
        width: 250,
      },
      errorText: {
        color: "red",
      },
    },
  };

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchRoles = () => {
    const getAllRoleApi = `${API_BASE_URL}${GET_ALL_ROLES}`;
    axios
      .get(getAllRoleApi, { headers: header })
      .then((response) => {
        console.log("Fetch assignedRoles successful", response.data);
        if (role !== "SUPERADMIN") {
          // Filter out the SUPERADMIN role from the roles array
          const filteredRoles = response.data.filter(
            (role) => role.name !== "SUPERADMIN"
          );
          setClientRole(filteredRoles);
        } else {
          setClientRole(response.data);
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };
  useEffect(() => {
    fetchRoles();
  }, []);

  //
  const handleTogglePasswordVisibility = () => {
    setShowPassword((prevShowPassword) => !prevShowPassword);
  };

  const handleSubmit = (event) => {
    const createClientApi = `${API_BASE_URL}${CREATE_CLIENT}`;
    setIsLoading(true);
    if (!hasErrors) {
      axios
        .post(
          createClientApi,
          {
            clientName: clientInput.clientName,
            spocName: clientInput.spocName,
            email: clientInput.email,
            password: clientInput.password,
            address: clientInput.address,
            phonenumber: clientInput.phonenumber,
            bussinessnumber: clientInput.bussinessnumber,
            assignedRoles: clientInput.assignedRoles,
            assignedRoleName: clientInput.assignedRoleName,
          },
          { headers: header }
        )
        .then((response) => {
          setData([...data, response.data]);
          console.log("POST request successful", response.data);
          handleClientCreated(response.data);

          message.success(
            "Client created successfully and password reset link has been sent to your email id"
          );
        })

        .catch((error) => {
          console.log("Client creation failed", error.response);

          setTimeout(() => {
            message.error("Client creation failed");
          }, 500);

          if (error.response && error.response.status === 409) {
            message.error("A client with the same email already exists.");
          } else if (error.response && error.response.status === 500) {
            message.error(
              "An error occurred while creating. Please try again later."
            );
          } else {
            message.error("Error creating data.");
          }
        })
        .finally(() => {
          setIsLoading(false);
        });

      // CLEAR THE FORM AFTER SUBMISSION
      setData({
        spocName: "",
        email: "",
        password: "",
        address: "",
        phonenumber: "",
        bussinessnumber: "",
        assignedRoles: "",
        assignedRoleName: "",
        active: "",
      });
    }
  };

  return (
    <div>
      <Dialog maxWidth="xs" open={!showClientModal}>
        <DialogTitle display={"flex"} sx={{ m: 0, p: 2 }}>
          <Container>
            <div>Create client</div>
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
                  label="clientName"
                  name="clientName"
                  variant="outlined"
                  size="small"
                  required
                  value={clientInput.clientName}
                  onChange={handleChange}
                  error={!!errors.clientName}
                  helperText={errors.clientName}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <TextField
                  id="outlined-basic"
                  label="spocName"
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
                  variant="outlined"
                  size="small"
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
                  name="assignedRoleName"
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
                  {clientRole.map((name) => (
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
          </Grid>
        </DialogContent>
        <Container>
          <DialogActions sx={{ margin: "5px", justifyContent: "flex-start" }}>
            <Button
              type="submit"
              id="UserModuleBtn"
              autoFocus
              onClick={() => {
                handleSubmit();
                handleClose();
              }}
              disabled={
                [
                  "clientName",
                  "spocName",
                  "email",
                  "password",
                  "address",
                  "phonenumber",
                  "bussinessnumber",
                ].some((field) => clientInput[field].trim() === "") ||
                clientInput.assignedRoles.length === 0 ||
                Object.values(errors).some((error) => !!error)
              }
            >
              Create
              <div style={{ display: "flex", alignItems: "center" }}>
                {isLoading && (
                  <ClipLoader color="white" loading={isLoading} size={20} />
                )}
              </div>
            </Button>
          </DialogActions>
        </Container>
      </Dialog>
    </div>
  );
}

export default CreateClient;