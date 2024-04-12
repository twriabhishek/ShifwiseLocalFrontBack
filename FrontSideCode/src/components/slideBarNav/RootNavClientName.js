import { message } from "antd";
import axios from "axios";
import React, { useState } from "react";
import { FaRegUser } from "react-icons/fa";
import { RiShutDownLine } from "react-icons/ri";
import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { ClipLoader } from "react-spinners";
import { API_BASE_URL, LOGOUT_API } from "../constant-API/constants";
function RootNavClientName() {
  // Don't show Exato Logo when we are in the home page
  const navigate = useNavigate();
  const location = useLocation();
  const isHomePage = location.pathname === "/shiftwise/slide/home";

  const clientName = localStorage.getItem("clientName");
  const [userName, setUserName] = useState({
    username: "",
  });
  const username = localStorage.getItem("username");
  const [profileData, setProfileData] = useState({});
  const userId = localStorage.getItem("userId");
  const [loading, setLoading] = useState(true);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  //profile
  const handleProfileClick = () => {
    axios
      .get(
        `${API_BASE_URL}/user/${userId}`,
        { headers: header }
      )
      .then((response) => {
        setProfileData(response.data);
      })
      .catch((error) => {
        // console.log("Get request by id failed", error);
      });
  };

  const handleLogout = () => {
    message.destroy();
    setLoading(true);
    localStorage.removeItem("token");
    localStorage.removeItem("clientId");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");
    localStorage.removeItem("businessUnit");
    localStorage.removeItem("clientName");
    localStorage.removeItem("roles");

    axios.post(`${API_BASE_URL}${LOGOUT_API}`, {
      username: username,
    },
      { headers: header }
    )
      .then((response) => {
        setUserName(response.data);
        localStorage.removeItem("token");

        setTimeout(() => {
          message.success("Loggedout!");
        }, 500);
        setLoading(false);
        navigate("/shiftwise");
      })

      .catch((error) => {
        console.log("Logout failed", error.response);

        setTimeout(() => {
          message.error("Logout failed");
        }, 500);
        setLoading(false);
      });
  };
  return (
    <>
      <div id="RootClientName">
        <div className="clientname ">
          <div className="row ">
            {isHomePage ? (
              <>
                <div className="col-10 text-center">
                  <p id="client-name" className="" style={{ fontSize: "30px" }}>
                    {clientName}
                  </p>
                </div>

                <div className="col-1 dropstart">
                  <div
                    className="dropdown rootNavUser mt-2"
                    id="rootNavSection"
                    style={{ cursor: "pointer" }}
                  >
                    <span className='fs-4 className="dropdown-toggle" data-bs-toggle="dropdown"'>
                      <FaRegUser />
                    </span>
                    <ul
                      className="dropdown-menu"
                      style={{
                        position: "absolute",
                        right: "0",
                        textAlign: "center",
                      }}
                    >
                      <li className="dropdown-item">
                        <NavLink
                          to="profile"
                          className="fs-5 submenu_icons ms-0 m-0 p-0"
                          onClick={handleProfileClick}
                        >
                          <div
                            style={{ display: "flex", alignItems: "center" }}
                          >
                            <FaRegUser />
                            <span
                              className="icons_font_size fs-6"
                              style={{ marginLeft: "5px", marginTop: "0px" }}
                            >
                              Profile
                            </span>
                          </div>
                        </NavLink>
                      </li>
                      <li className="dropdown-item">
                        <NavLink
                          to="/"
                          className="fs-5 submenu_icons"
                          onClick={handleLogout}
                        >
                          <div
                            style={{ display: "flex", alignItems: "center" }}
                          >
                            <RiShutDownLine />
                            <span
                              className="icons_font_size fs-6"
                              style={{ marginLeft: "5px" }}
                            >
                              Logout
                            </span>
                            {loading && (
                              <span style={{ marginLeft: "5px" }}>
                                <ClipLoader
                                  color="white"
                                  loading={loading}
                                  size={20}
                                />
                              </span>
                            )}
                          </div>
                        </NavLink>
                      </li>
                    </ul>
                  </div>
                </div>
              </>
            ) : (
              <>
                <div className="col-1 text-center">
                  <div className="logo">
                    <NavLink to="home">
                      <img
                        src="../img/Exato_logo.png"
                        className="companyLogo"
                        alt=""
                      />
                    </NavLink>
                  </div>
                </div>

                <div className="col-10 text-center">
                  <p id="client-name" className="" style={{ fontSize: "30px" }}>
                    {clientName}
                  </p>
                </div>

                <div className="col-1 dropstart">
                  <div
                    className="dropdown rootNavUser mt-2"
                    id="rootNavSection"
                    style={{ cursor: "pointer" }}
                  >
                    <span className='fs-4 className="dropdown-toggle" data-bs-toggle="dropdown"'>
                      <FaRegUser />
                    </span>
                    <ul
                      className="dropdown-menu"
                      style={{
                        position: "absolute",
                        right: "0",
                        textAlign: "center",
                      }}
                    >
                      <li className="dropdown-item">
                        <NavLink
                          to="profile"
                          className="fs-5 submenu_icons ms-0 m-0 p-0"
                        >
                          <div
                            style={{ display: "flex", alignItems: "center" }}
                          >
                            <FaRegUser />
                            <span
                              className="icons_font_size fs-6"
                              style={{ marginLeft: "5px" }}
                            >
                              Profile
                            </span>
                          </div>
                        </NavLink>
                      </li>
                      <li className="dropdown-item">
                        <NavLink
                          // to="/"
                          className="fs-5 submenu_icons"
                          onClick={handleLogout}
                        >
                          <div
                            style={{ display: "flex", alignItems: "center" }}
                          >
                            <RiShutDownLine />
                            <span
                              className="icons_font_size fs-6"
                              style={{ marginLeft: "5px" }}
                            >
                              Logout
                            </span>
                            {loading && (
                              <span style={{ marginLeft: "5px" }}>
                                <ClipLoader
                                  color="white"
                                  loading={loading}
                                  size={20}
                                />
                              </span>
                            )}
                          </div>
                        </NavLink>
                      </li>
                    </ul>
                  </div>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </>
  );
}

export default RootNavClientName;
