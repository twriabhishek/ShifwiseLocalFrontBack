import {
  Avatar,
  Button,
  FormGroup,
  Grid,
  Paper,
  TextField,
} from "@mui/material";
import React, { useState } from "react";
import { Form } from "react-bootstrap";
import LockClockOutlinedIcon from "@mui/icons-material/LockClockOutlined";
import axios from "axios";
import { message } from "antd";
import { useNavigate, useParams } from "react-router-dom";
import { ClipLoader } from "react-spinners";
import { API_BASE_URL, RESET_PASSWORD } from "../constant-API/constants";

function ResetPassword() {
  const { email } = useParams();
  const { token } = useParams();
  const [password, setPassword] = useState({
    newPassword: "",
    confirmPassword: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [resetStatus, setResetStatus] = useState("");
  const [hasErrors, setHasErrors] = useState(false);
  const [errors, setErrors] = useState({
    newPassword: "",
    confirmPassword: "",
  });
  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value } = event.target;

    let error = "";
    if (name === "newPassword" && value.trim() === "") {
      error = "Password is required";
    } else if (
      name === "newPassword" &&
      !/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{8,}$/.test(value)
    ) {
      error =
        "Password must be at least 8 characters and contain at least one uppercase letter and one number";
    }
    if (name === "confirmPassword" && value.trim() === "") {
      error = "Password is required";
    }
    setErrors({
      ...errors,
      [name]: error,
    });
    setPassword({ ...password, [name]: value });
  };

  const header = {
    Authorization: `${token}`,
  };

  const handleSubmit = (e) => {
    if (!hasErrors) {
      e.preventDefault();
      setIsLoading(true);
      if (password.newPassword !== password.confirmPassword) {
        setResetStatus("Passwords do not match");
        message.error("Passwords do not match");
        setIsLoading(false);
        setPassword({ newPassword: "", confirmPassword: "" });
        return;
      }

      axios
        .post(
          `${API_BASE_URL}${RESET_PASSWORD}?email=${email}`,
          {
            email: email,
            newPassword: password.newPassword,
          },
          { headers: header }
        )
        .then((response) => {
          if (response.status === 200) {
            console.log("Password reset successfull", response.data);
            setResetStatus("Password reset successfull", response.data);
            message.success("Password reset successfully");
            navigate("/shiftwise");
          } else {
            setResetStatus("Failed to reset password");
            message.error("Failed to reset password");
          }
          setPassword({ newPassword: "", confirmPassword: "" });
        })
        .catch((error) => {
          console.error(error);
          console.log("error occured", error);
          setResetStatus("An error occurred", error);
          message.error("Failed to reset password");
          setPassword({ newPassword: "", confirmPassword: "" });
        })

        .finally(() => {
          setIsLoading(false);

          setPassword({ newPassword: "", confirmPassword: "" });
        });
    }
  };

  return (
    <div>
      <Grid style={{ margin: "30px" }}>
        <Paper elevation={10} className="paper">
          <Grid align="center">
            <Avatar className="avatar" style={{ backgroundColor: "#41b18c" }}>
              <LockClockOutlinedIcon />
            </Avatar>
            <h4 className="py-3">Reset password</h4>
          </Grid>
          <Form onSubmit={handleSubmit} style={{ margin: "20px" }}>
            <FormGroup>
              <TextField
                label="New password"
                name="newPassword"
                // value={formData.username}
                variant="standard"
                fullWidth
                required
                onChange={handleChange}
                error={!!errors.newPassword}
                helperText={errors.newPassword}
              />

              <TextField
                label="Confirm password"
                type="password"
                // value={formData.password}
                name="confirmPassword"
                variant="standard"
                fullWidth
                required
                onChange={handleChange}
              />
              <Button
                style={{ margin: "30px auto" }}
                className="buttonStyling"
                variant="contained"
                id="UserModuleBtn"
                fullWidth
                type="submit"
                disabled={
                  !password.newPassword.trim() ||
                  !password.confirmPassword.trim() ||
                  Object.values(errors).some((error) => !!error)
                }
              >
                Reset password
              </Button>
              {isLoading && (
                <div
                  className="loading-spinner"
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    height: "100px",

                    color: "white",
                  }}
                >
                  <ClipLoader color="#123abc" loading={isLoading} size={50} />
                </div>
              )}
            </FormGroup>
          </Form>
        </Paper>
      </Grid>
    </div>
  );
}

export default ResetPassword;
