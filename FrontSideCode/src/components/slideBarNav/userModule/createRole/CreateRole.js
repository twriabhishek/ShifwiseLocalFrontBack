import {
  Button,
  Container,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  IconButton,
  TextField,
} from "@mui/material";
import React, { useState } from "react";
import CloseIcon from "@mui/icons-material/Close";
import axios from "axios";
import { message } from "antd";
import { Form } from "react-bootstrap";
import { API_BASE_URL, CREATE_ROLE } from "../../../constant-API/constants";

function CreateRole(props) {
  const {
    children,
    showRoleModal,
    handleClose,
    setShowRoleModal,
    handleRoleCreated,
    ...other
  } = props;
  const [role, setRole] = useState({
    name: "",
  });
  const [errors, setErrors] = useState({
    name: "",
  });
  const handleChange = (event) => {
    const { name, value } = event.target;

    //validation
    let error = "";
    if (name === "name" && value.trim() === "") {
      error = "Name is required";
    } else if (name === "name" && !/^[A-Za-z\s-]*$/.test(value)) {
      error = "Invalid Name";
    }

    setErrors({
      ...errors,
      [name]: error,
    });

    setRole({ ...role, [name]: value });
  };
  const [data, setData] = useState([]);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  //posting
  const handleSubmit = (event) => {
    const roleApi = `${API_BASE_URL}${CREATE_ROLE}`;

    event.preventDefault();
    console.log("Hello");
    axios
      .post(
        roleApi,
        {
          name: role.name,
        },
        { headers: header }
      )
      .then((response) => {
        console.log("POST request successful:", response.data);
        handleRoleCreated(response.data);

        setData([...data, response.data]);

        setTimeout(() => {
          message.success("Created successfully");
        }, 500);
        handleClose();
      })

      .catch((error) => {
        console.log("Role creation failed", error.response);

        setTimeout(() => {
          message.error("Role creation failed");
        }, 500);
      });

    // CLEAR THE FORM AFTER SUBMISSION

    setRole({
      name: "",
    });
    console.log(role);
  };

  return (
    <div>
      <Dialog maxWidth="xs" open={!showRoleModal}>
        <DialogTitle display={"flex"} sx={{ m: 0, p: 2 }}>
          <Container>
            <div>Create role</div>
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

        <DialogContent
          dividers
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Container
            fixed
            sx={{
              display: "flex",
              flexFlow: "wrap",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Form onSubmit={handleSubmit}>
              <FormControl
                sx={{
                  margin: "5px",
                  marginTop: "5px",
                  minWidth: "100%",
                  maxWidth: "100%",
                }}
              >
                <TextField
                  id="outlined-basic"
                  label="Name"
                  size="small"
                  name="name"
                  variant="outlined"
                  onChange={handleChange}
                  error={!!errors.name}
                  helperText={errors.name}
                />
              </FormControl>

          <DialogActions sx={{ margin: "5px" , justifyContent: "flex-start"}}>
            <Button
              type="submit"
              id="UserModuleBtn"
              autoFocus
              // onClick={() => {
              //   handleSubmit();
              //   handleClose();
              // }}
              disabled={
                Object.values(role).some((field) => field === "") ||
                Object.values(errors).some((error) => !!error)
              }
            >
              Create
            </Button>
          </DialogActions>
          </Form>
        </Container>
        </DialogContent>
      </Dialog>
    </div>
  );
}

export default CreateRole;
