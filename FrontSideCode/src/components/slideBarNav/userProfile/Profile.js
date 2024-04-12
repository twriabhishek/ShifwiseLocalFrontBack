import { Avatar, Button, Card, Grid, Typography } from "@mui/material";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import LogoutIcon from "@mui/icons-material/Logout";
import PersonIcon from "@mui/icons-material/Person";
import EmailIcon from "@mui/icons-material/Email";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import PhoneIcon from "@mui/icons-material/Phone";
import BusinessIcon from "@mui/icons-material/Business";
import GroupIcon from "@mui/icons-material/Group";
import SettingsApplicationsIcon from "@mui/icons-material/SettingsApplications";
import GroupsIcon from "@mui/icons-material/Groups";
import { message } from "antd";
import { ClipLoader } from "react-spinners";
import { FormCheck } from "react-bootstrap";
import { API_BASE_URL } from "../../constant-API/constants";

const Profile = () => {
  const [userName, setUserName] = useState({
    username: "",
  });
  const [userData, setUserData] = useState({});
  const [loading, setLoading] = useState(true);
  const toggler = (id) => { };
  localStorage.getItem("clientId");

  const userId = localStorage.getItem("userId");

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  useEffect(() => {
    axios.get(`${API_BASE_URL}/user/${userId}`, { headers: header }).then((response) => {
      setUserData(response.data);
      setLoading(false);
    })
      .catch((error) => {
        console.log("Get request by id failed", error);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [userId]);

  const navigate = useNavigate();

  const handleLogout = () => {
    setLoading(true);
    localStorage.removeItem("token");
    localStorage.removeItem("clientId");
    localStorage.removeItem("userId");

    axios
      .post(
        `${API_BASE_URL}/logout`,
        {
          username: userName.username,
        },
        { headers: header }
      )
      .then((response) => {
        setUserName(response.data);
        localStorage.removeItem("token");

        setTimeout(() => {
          message.success("Loggedout!");
        }, 100);
        setLoading(false);
        navigate("/");
      })

      .catch((error) => {
        console.log("Logout failed", error.response);

        setTimeout(() => {
          message.error("Logout failed");
        }, 500);
      });
  };

  const clientName = localStorage.getItem("clientName");

  return (
    <div className="profile-container">
      <Grid>
        <Card
          id="profile"
          elevation={10}
          style={{ overflowY: "hidden", alignItems: "center" }}
        >
          <Grid align="center">
            <Avatar className="avatar-enhancements" style={{
              margin: "15px",
              backgroundColor: "#808080",
              width: "80px",
              height: "80px",
            }}
              src="/broken-image.jpg"
            />
            <h2> {clientName}</h2>
          </Grid>
          {loading ? (
            <div style={{ textAlign: "center" }}>
              <ClipLoader color="#36D7B7" loading={loading} size={20} />
            </div>
          ) : (
            userData && (
              <div className="profile_fields">
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <PersonIcon />{" "}
                  <span style={{ fontWeight: "bold" }}>Name : </span>{" "}
                  {userData.firstName}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <EmailIcon />{" "}
                  <span style={{ fontWeight: "bold" }}>Email : </span>{" "}
                  {userData.email}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <LocationOnIcon />
                  <span style={{ fontWeight: "bold" }}>Address : </span>{" "}
                  {userData.address}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <PhoneIcon />{" "}
                  <span style={{ fontWeight: "bold" }}>Phonenumber : </span>{" "}
                  {userData.phonenumber}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <BusinessIcon />{" "}
                  <span style={{ fontWeight: "bold" }}>Businessnumber : </span>{" "}
                  {userData.bussinessnumber}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <GroupIcon style={{ marginRight: "8px" }} />{" "}
                  <span style={{ fontWeight: "bold" }}>Assigned Roles : </span>
                  {userData.assignedRoleName}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <BusinessIcon style={{ marginRight: "8px" }} />{" "}
                  <span style={{ fontWeight: "bold" }}>Businessunit : </span>
                  {userData.businessUnit}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <SettingsApplicationsIcon style={{ marginRight: "8px" }} />{" "}
                  <span style={{ fontWeight: "bold" }}>Processunit : </span>
                  {userData.processUnit}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <GroupsIcon style={{ marginRight: "8px" }} />{" "}
                  <span style={{ fontWeight: "bold" }}>Team : </span>
                  {userData.team}
                </Typography>
                <Typography
                  variant="body1"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "5px",
                  }}
                >
                  <GroupsIcon style={{ marginRight: "8px" }} />{" "}
                  <span style={{ fontWeight: "bold" }}>Group : </span>
                  {userData.group}
                </Typography>
                <Typography variant="body1" style={{
                  display: "flex",
                  alignItems: "center",
                  padding: "5px",
                }}
                >
                  <span style={{ fontWeight: "bold" }}>Status : </span>{" "}
                  {userData.active}
                  <FormCheck type="switch" label="" color="#36D7B7" checked={userData.active}
                    onChange={toggler(userData.id)}
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
                        style={{ textTransform: "none" }}
                      >
                        <ArrowBackIcon />
                        Go Back
                      </Button>
                    </Link>
                  </Grid>
                  <Grid item xs={12} sm={6}>
                    <Link to="/" className="mycolumn">
                      <Button
                        type="submit"
                        variant="contained"
                        id="UserModuleBtn"
                        style={{ textTransform: "none" }}
                        onClick={handleLogout}
                      >
                        <LogoutIcon />
                        Logout
                        {loading && (
                          <span style={{ marginLeft: "5px" }}>
                            <ClipLoader
                              color="white"
                              loading={loading}
                              size={20}
                            />
                          </span>
                        )}
                      </Button>
                    </Link>
                  </Grid>
                </Grid>
              </div>
            )
          )}
        </Card>
      </Grid>
      <div className="col-md-3 mb-3 col-sm-12 col-lg-3"></div>
    </div>
  );
};

export default Profile;
