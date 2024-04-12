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
import React, { useEffect, useState } from "react";
import CloseIcon from "@mui/icons-material/Close";
import axios from "axios";
import { message } from "antd";
import { API_BASE_URL, UPDATE_ROLE } from "../../../constant-API/constants";

function EditRole(props) {
  const {
    title,
    currRow,
    children,
    showRoleEditModel,
    closeDialog,
    handleClose,
    setShowRoleEditModel,
    editFunction,
    handleRoleUpdate,
    roleData,

    ...other
  } = props;

  const [roleInput, setRoleInput] = useState({
    id: "id",
    name: "",
  });
  const [errors, setErrors] = useState({
    name: "",
  });

  const [updatedData, setUpdatedData] = useState([]);

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
    setRoleInput({ ...roleInput, [event.target.name]: event.target.value });
  };

  useEffect(() => {
    if (roleData) {
      console.log("role data", roleData);
      console.log("role input", roleInput);
      setRoleInput({ ...roleInput, id: roleData.id, name: roleData.name });
    }
  }, [roleData]);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const handleSubmit = (event) => {
    const updateRoleApi = `${API_BASE_URL}${UPDATE_ROLE}/${roleData.id}`;
    axios
      .put(updateRoleApi, roleInput, { headers: header })
      .then((response) => {
        setUpdatedData(response.data);
        console.log("PUT request successful", response.data);
        handleRoleUpdate(response.data);
        setTimeout(() => {
          message.success("Role updated successfully");
        }, 500);
      })
      .catch((error) => {
        console.log("PUT request failed", error);
      });
  };

  return (
    <div>
      <Dialog maxWidth="xs" open={!showRoleEditModel}>
        <DialogTitle display={"flex"} sx={{ m: 0, p: 2 }}>
          <Container>
            <div>Edit role</div>
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
                label="Id"
                name="id"
                size="small"
                disabled
                variant="outlined"
                onChange={handleChange}
                value={roleInput.id}
              />
            </FormControl>

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
                name="name"
                size="small"
                variant="outlined"
                onChange={handleChange}
                value={roleInput.name}
                error={!!errors.name}
                helperText={errors.name}
              />
            </FormControl>
          </Container>
        </DialogContent>
        <Container>
          <DialogActions sx={{ margin: "5px" }}>
            <Button
              type="submit"
              // variant="contained"
              // color="success"
              id="UserModuleBtn"
              autoFocus
              onClick={() => {
                handleSubmit();
                handleClose();
              }}
              disabled={
                Object.values(roleInput).some((field) => field === "") ||
                Object.values(errors).some((error) => !!error)
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

export default EditRole;
