import { Avatar, Button, Grid, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import axios from "axios";
import Card from "@mui/material/Card";
import { message } from "antd";
import { FormCheck } from "react-bootstrap";
import {
  API_BASE_URL,
  GET_USER_BY_ID,
  RESEND_RESET_PASSWORD_LINK,
} from "../../../constant-API/constants";

function ViewUser() {
  const { id } = useParams();
  console.log("id:", id);
  const [userDetails, setUserDetails] = useState({});
  const toggler = (id) => {
    //toggles based on acive/inactive state
  };
  const [requestResendLink, setRequestResendLink] = useState({});

  const header = {
    Authorization: localStorage.getItem("token"),
  };
  useEffect(() => {
    console.log("component view user");
  }, []);

  useEffect(() => {
    const api_Url = `${API_BASE_URL}${GET_USER_BY_ID}/${id}`;
    axios
      .get(
        api_Url,

        { headers: header }
      )
      .then((response) => {
        console.log("Get request by id successful", response.data);
        setUserDetails(response.data);
      })
      .catch((error) => {
        console.log("Get request by id failed", error);
      });
  }, []);

  const handleResendRequestLink = () => {
    const resendPasswordResetApi = `${API_BASE_URL}${RESEND_RESET_PASSWORD_LINK}/${id}`;
    axios
      .post(
        resendPasswordResetApi,

        { email: userDetails.email },
        { headers: header }
      )
      .then((response) => {
        console.log("Resend reset password link successful", response.data);
        setRequestResendLink(response.data);
        message.success("Reset password link sent successfully!");
      })
      .catch((error) => {
        console.log("Resend reset password link failed", error);
        message.error("Reset password link failed!");
      });
  };
  return (
    <div className="centeredCard">
      <Grid id="card">
        <Card elevation={10}>
          <Grid align="center">
            <Avatar style={{ margin: "15px" }} src="/broken-image.jpg" />
            <h2>User details</h2>
          </Grid>
          {userDetails && (
            <div className="user">
              <Typography variant="body1" style={{ display: "inline" }}>
                <span style={{ fontWeight: "bold" }}>User ID : </span>
                {userDetails.id}
              </Typography>
              <Typography variant="body1" style={{ display: "block" }}>
                <span style={{ fontWeight: "bold" }}>Client ID : </span>
                {userDetails.clientId}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>User name : </span>
                {userDetails.firstName} {userDetails.lastName}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Email : </span>
                {userDetails.email}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Address : </span>
                {userDetails.address}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Phonenumber : </span>
                {userDetails.phonenumber}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Businessnumber : </span>
                {userDetails.bussinessnumber}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Assigned roles : </span>
                {userDetails.assignedRoleName}
              </Typography>
              <Typography variant="body1" style={{ display: "inline" }}>
                <span style={{ fontWeight: "bold" }}>Businessunit: </span>
                {userDetails.businessUnit}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Processunit : </span>
                {userDetails.processUnit}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Team : </span>
                {userDetails.team}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Group : </span>
                {userDetails.group}
              </Typography>
              <Typography variant="body1">
                <span style={{ fontWeight: "bold" }}>Status : </span>
                {userDetails.active}
                <FormCheck
                  type="switch"
                  label=""
                  color="#36D7B7"
                  checked={userDetails.active}
                  onChange={toggler(userDetails.id)}
                  className="custom-switch"
                />
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Link className="btn-btn-danger" to="../createuser">
                    <Button
                      type="submit"
                      variant="contained"
                      id="UserModuleBtn"
                      color="error"
                      style={{ textTransform: "none" }}
                    >
                      Go Back
                    </Button>
                  </Link>
                </Grid>
                <Grid item xs={12} sm={6}>
                  <Button
                    type="submit"
                    variant="contained"
                    id="UserModuleBtn"
                    color="error"
                    style={{ textTransform: "none" }}
                    onClick={() => {
                      handleResendRequestLink();
                    }}
                  >
                    Resend reset link
                  </Button>
                </Grid>
              </Grid>
            </div>
          )}
        </Card>
      </Grid>
    </div>
  );
}

export default ViewUser;
