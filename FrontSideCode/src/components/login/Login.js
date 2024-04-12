import {
  Avatar,
  Button,
  Checkbox,
  FormControlLabel,
  FormGroup,
  Grid,
  Link,
  Paper,
  TextField,
  Typography,
} from "@mui/material";
import React, { useState } from "react";
import { Form } from "react-bootstrap";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import axios from "axios";
import { message } from "antd";
import { useNavigate } from "react-router-dom";
import { ClipLoader } from "react-spinners";
import { API_BASE_URL, LOGIN_API } from "../constant-API/constants";

function Login() {
  const apiUrl = `${API_BASE_URL}${LOGIN_API}`;
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });
  const [error, setError] = useState({
    username: "",
    password: "",
  });
  const [submittedData, setSubmittedData] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsLoading(true);
    try {

      const response = await axios.post(apiUrl, formData);
      setSubmittedData([...submittedData, response.data]);
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("clientId", response.  data.clientId);
      localStorage.setItem("clientName", response.data.clientName);
      localStorage.setItem("roles", response.data.roles);
      localStorage.setItem("userId", response.data.userId);
      localStorage.setItem("businessUnit", response.data.businessUnit);
      localStorage.setItem("username", response.data.username);     

      if (response.data.invalidCredentials) {
        setError({
          username: "Invalid username or password",
          password: "",
          general: "",
        });
        message.error("Invalid username or password");
      } else {
        message.success("Login successful");
        navigate("/shiftwise/slide/home");
      }
    } catch (err) {
      console.log(err);
      if (err.response) {
        if (err.response.status === 401) {
          setError({
            username: "Invalid username or password",
            password: "",
            general: "",
          });
          message.error("Invalid username or password");
        } else {
          setError({ username: "", password: "", general: "Server error" });
        }
      }
    } finally {
      // Clear loading when the request is complete
      setIsLoading(false);
    }

    // Clearing form data
    setFormData({
      username: "",
      password: "",
    });
  };

  return (
    <div className="" id="loginId">
      <Grid>
        <Paper elevation={10} className="paper">
          <Grid align="center">
            <Avatar className="avatar" style={{ backgroundColor: "#41b18c" }}>
              <AccountCircleIcon />
            </Avatar>
            <h4>Login </h4>
          </Grid>
          <div className="row">
            <div className="col-md-12">
              <Form onSubmit={handleSubmit}>
                <FormGroup>
                  <TextField
                    // id="standard-basic"
                    label="Username"
                    name="username"
                    value={formData.username}
                    variant="standard"
                    fullWidth
                    required
                    onChange={handleChange}
                  />

                  <TextField
                    // id="standard-basic"
                    label="Password"
                    type="password"
                    value={formData.password}
                    name="password"
                    variant="standard"
                    fullWidth
                    required
                    onChange={handleChange}
                  />

                  <FormControlLabel
                    control={<Checkbox />}
                    label="Remember me"
                  />
                  <Button
                    style={{ margin: "8px auto" }}
                    variant="contained"
                    fullWidth
                    type="submit"
                    className="buttonStyling"
                    id="UserModuleBtn"
                  >
                    Login
                  </Button>
                  <Typography style={{ margin: "8px auto" }}>
                    <Link href="/shiftwise/forgot-password">Forgot password</Link>
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
                      <ClipLoader
                        color="#123abc"
                        loading={isLoading}
                        size={50}
                      />
                    </div>
                  )}
                </FormGroup>
              </Form>
            </div>
          </div>
        </Paper>
      </Grid>
    </div>
  );
}

export default Login;
