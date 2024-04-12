import { Avatar, CardHeader, IconButton } from "@mui/material";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Button, Card, FormCheck } from "react-bootstrap";
import { Link, useParams } from "react-router-dom";
import PersonIcon from "@mui/icons-material/Person";
import EmailIcon from "@mui/icons-material/Email";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import PhoneIcon from "@mui/icons-material/Phone";
import GroupsIcon from "@mui/icons-material/Groups";
import { message } from "antd";
import CloseIcon from "@mui/icons-material/Close";
import {
  API_BASE_URL,
  CLIENT_RESEND_RESET_PASSWORD_LINK,
  GET_CLIENT_BY_CLIENTID,
} from "../../../constant-API/constants";

function ViewClient() {
  const { clientId } = useParams();
  const [clientDetails, setClientDetails] = useState({});
  const [requestResendLink, setRequestResendLink] = useState({});
  
  const toggler = (clientId) => {
    //toggles based on acive/inactive state
  };
  const role = localStorage.getItem("roles");

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  useEffect(() => {
    const clientByIdApi = `${API_BASE_URL}${GET_CLIENT_BY_CLIENTID}/${clientId}`;
    axios
      .get(clientByIdApi, { headers: header })
      .then((response) => {
        console.log("Get request by client id successful", response.data);
        setClientDetails(response.data);
      })
      .catch((error) => {
        console.log("Get request by client id failed", error);
      });
  }, []);

  const handleResendRequestLink = () => {
    const resendPasswordResetApi = `${API_BASE_URL}${CLIENT_RESEND_RESET_PASSWORD_LINK}/${clientId}`;
    axios
      .post(
        resendPasswordResetApi,
        { email: clientDetails.email },
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
    <div className="center-container">
      <Card className="view-client" elevation={10}>
        <CardHeader
          action={
            <IconButton
              component={Link}
              to={role === "SUPERADMIN" ? "../createclient" : "../client"}
            >
              <CloseIcon />
            </IconButton>
          }
        />
        <div
          style={{
            margin: "15px",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <Avatar src="/broken-image.jpg" />
        </div>

        <Card.Body>
          <Card.Title id="client-title">Client Details</Card.Title>
          <div className="client-details">
            <Card.Text>
              <PersonIcon />
              <span className="fw-bold text-muted">Client :</span>{" "}
              {clientDetails.clientName}
            </Card.Text>
            <Card.Text>
              <PersonIcon />
              <span className="fw-bold text-muted">Spoc Name :</span>{" "}
              {clientDetails.spocName}
            </Card.Text>
            <Card.Text>
              <EmailIcon /> <span className="fw-bold text-muted">Email :</span>{" "}
              {clientDetails.email}
            </Card.Text>
            <Card.Text>
              <PhoneIcon />{" "}
              <span className="fw-bold text-muted">Phonenumber :</span>{" "}
              {clientDetails.phonenumber}
            </Card.Text>
            <Card.Text>
              <PhoneIcon />{" "}
              <span className="fw-bold text-muted">Businessnumber :</span>{" "}
              {clientDetails.bussinessnumber}
            </Card.Text>
            <Card.Text>
              <LocationOnIcon />
              <span className="fw-bold text-muted">Address :</span>{" "}
              {clientDetails.address}
            </Card.Text>
            <Card.Text>
              <GroupsIcon />{" "}
              <span className="fw-bold text-muted">Assigned roles :</span>{" "}
              {clientDetails.assignedRoleName}
            </Card.Text>
            <Card.Text style={{ display: "flex", alignItems: "center" }}>
              <PersonIcon style={{ marginRight: "8px" }} />
              <span
                className="fw-bold text-muted"
                style={{ marginRight: "8px" }}
              >
                Status:
              </span>
              {clientDetails.active}
              <FormCheck
                type="switch"
                label=""
                color="#36D7B7"
                checked={clientDetails.active}
                onChange={toggler(clientDetails.id)}
                className="custom-switch"
              />
            </Card.Text>
          </div>
        </Card.Body>

        <Card.Body className="client-footer">
          {/* <Card.Link as={Link} to={role === "SUPERADMIN" ? "../createclient" : "../client"}>
            <Button
              type="submit"
              variant="danger"
              color="error"
              style={{ textTransform: "none" }}
            >
              Go Back
            </Button>
          </Card.Link> */}
          <Card.Link
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <Button
              type="submit"
              variant="contained"
              id="UserModuleBtn"
              style={{fontSize:'15px'}}
              onClick={() => {
                handleResendRequestLink();
              }}
            >
              Resend password reset link
            </Button>
          </Card.Link>
        </Card.Body>
      </Card>
    </div>
  );
}

export default ViewClient;
