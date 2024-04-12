import {
  Avatar,
  Button,
  FormControl,
  Grid,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import LockClockOutlinedIcon from "@mui/icons-material/LockClockOutlined";
import { Form } from "react-bootstrap";
import axios from "axios";
import { message } from "antd";
import { useNavigate, useParams } from "react-router-dom";
import { ClipLoader } from "react-spinners";
import { API_BASE_URL, FORGOT_PASSWORD_API, VERIFY_OTP } from "../constant-API/constants";

function VerifyOTP() {
  const { email } = useParams();
  const [remainingTime, setRemainingTime] = useState(60);
  const [isTimerActive, setIsTimerActive] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [isOtpSent, setIsOtpSent] = useState(false);

  const [otpNumber, setOTPNumber] = useState(0);
  const [verificationStatus, setVerificationStatus] = useState("");
  const navigate = useNavigate();

  //Function to start timer
  const startTimer = () => {
    setIsTimerActive(true);
    setRemainingTime(60);
  };

  useEffect(() => {
    let timer;

    if (isTimerActive && remainingTime > 0) {
      timer = setTimeout(() => {
        setRemainingTime(remainingTime - 1);
      }, 1000);
    } else if (remainingTime === 0) {
      setIsTimerActive(false);
    }

    return () => {
      clearTimeout(timer);
    };
  }, [isTimerActive, remainingTime]);

  const handleOtpSubmit = (event) => {
    event.preventDefault();
    setIsLoading(true);

    axios
      .post(
        `${API_BASE_URL}${VERIFY_OTP}`,
        {
          otpNumber: otpNumber,
          userEmail: email,
        }
      )
      .then((response) => {
        if (response.status === 200) {
          setVerificationStatus("OTP verified successfully.", response.data);
          message.success("OTP verified successfully");
          setIsOtpSent(true);
          navigate("/shiftwise/change-password/" + email);
          setOTPNumber(0);
        } else {
          setVerificationStatus("Failed to verify OTP. Please try again.");
          message.error("Failed to verify OTP");
        }
        setOTPNumber(0); // Clear the OTP input
      })
      .catch((error) => {
        console.error(error);
        setVerificationStatus("An error occurred. Please try again later.");
        message.error("Failed to verify OTP");
        setIsLoading(false);
        setOTPNumber(0);
      });
  };

  //Resend otp
  const handleResendOtp = () => {
    setIsLoading(true);

    axios
      .post(
        `${API_BASE_URL}${FORGOT_PASSWORD_API}`,
        {
          email: email,
        }
      )
      .then((response) => {
        if (response.status === 200) {
          message.success("OTP resent successfully to your email!");
          setIsOtpSent(true);
          startTimer(); // Start the timer after resending OTP
          setIsTimerActive(true);
        } else {
          message.error("Failed to resend OTP");
        }
      })
      .catch((error) => {
        console.error(error);
        message.error("Failed to resend OTP");
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  return (
    <div>
      <Grid style={{ margin: "30px" }}>
        <Paper elevation={10} className="paper" maxWidth="xs">
          <Grid align="center">
            <Avatar className="avatar" style={{ backgroundColor: "#41b18c" }}>
              <LockClockOutlinedIcon />
            </Avatar>
            <h4 className="pt-3">Verify OTP</h4>
          </Grid>

          <Form onSubmit={handleOtpSubmit}>
            <Grid style={{ alignItems: "center", margin: "20px" }}>
              <FormControl>
                <TextField
                  label="OTP"
                  type="otp"
                  name="otpNumber"
                  variant="standard"
                  fullWidth
                  required
                  onChange={(e) => setOTPNumber(parseInt(e.target.value, 10))}
                />
              </FormControl>
            </Grid>
            <Typography
              variant="h6"
              style={{
                fontSize: "15px",
                color: "gray",
                alignItems: "center",
                marginLeft: "40px",
              }}
            >
              Enter your 4 digits OTP
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Button
                  style={{ margin: "20px auto" }}
                  variant="contained"
                  fullWidth
                  type="submit"
                  id="UserModuleBtn"
                  disabled={!isTimerActive}
                  className="buttonStyling"
                >
                  Submit
                </Button>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Button
                  style={{ margin: "20px auto" }}
                  variant="contained"
                  id="UserModuleBtn"
                  fullWidth
                  type="submit"
                  onClick={handleResendOtp}
                  disabled={isTimerActive || isLoading}
                >
                  Resend OTP
                </Button>
              </Grid>
            </Grid>

            <Typography variant="body2">
              {isTimerActive
                ? `OTP expires in ${remainingTime} seconds`
                : "OTP expired please request a new one."}
            </Typography>
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

export default VerifyOTP;
