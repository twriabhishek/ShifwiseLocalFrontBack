import {
  Avatar,
  Button,
  FormControl,
  Grid,
  Paper,
  TextField,
} from "@mui/material";
import React from "react";
import { Form } from "react-bootstrap";
import LockClockOutlinedIcon from "@mui/icons-material/LockClockOutlined";
import { useState } from "react";
import axios from "axios";
import { message } from "antd";
import { useNavigate } from "react-router-dom";
import { ClipLoader } from "react-spinners";
import { API_BASE_URL, FORGOT_PASSWORD_API } from "../constant-API/constants";

function ForgotPassword() {
  const [isEmail, setIsEmail] = useState({
    email: "",
  });
  const [otpStatus, setOTPStatus] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value } = event.target;
    console.log("data is:", isEmail);
    setIsEmail({ ...isEmail, [name]: value });
  };

  const handleResetPassword = (e) => {
    e.preventDefault();
    setIsLoading(true);

    axios
      .post(
        `${API_BASE_URL}${FORGOT_PASSWORD_API}`,
        {
          email: isEmail.email,
        }
      )
      .then((response) => {
        if (response.status === 200) {
          console.log("OTP sent successfully", response.data);
          setOTPStatus("OTP sent successfully to your email");
          message.success("OTP sent successfully to your email!");
          navigate("/otp/" + isEmail.email);
        } else {
          setOTPStatus("Failed to send OTP. Please try again");
          message.error("Failed to send OTP");
        }
        setIsEmail({ email: "" });
      })
      .catch((error) => {
        console.error(error);
        setOTPStatus("An error occurred. Please try again later.");
        message.error("Failed to send OTP");
        setIsEmail({ email: "" });
      })
      .finally(() => {
        setIsLoading(false);

        setIsEmail({ email: "" });
      });
  };
  return (
    <div className="forgetpassword">
      <Grid style={{ size: "xs" }}>
        <Paper elevation={10} className="paper" maxWidth="xs">
          <Grid align="center">
            <Avatar className="avatar" style={{ backgroundColor: "#41b18c" }}>
              <LockClockOutlinedIcon />
            </Avatar>
            <h4 className="py-3">Forgot password</h4>
          </Grid>
          <Form onSubmit={handleResetPassword}>
            <Grid style={{ alignItems: "center", margin: "10px" }}>
              <FormControl>
                <TextField
                  label="Enter your email"
                  name="email"
                  placeholder="Enter your email"
                  variant="outlined"
                  size="small"
                  style={{ width: "20rem" }}
                  // value={formData.username}
                  fullWidth
                  required
                  onChange={handleChange}
                />
              </FormControl>
            </Grid>
            <Button
              style={{ margin: "5px auto" }}
              variant="contained"
              fullWidth
              type="submit"
              id="UserModuleBtn"
              className="buttonStyling"
            >
              Submit
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
          </Form>
        </Paper>
      </Grid>
    </div>
  );
}

export default ForgotPassword;
