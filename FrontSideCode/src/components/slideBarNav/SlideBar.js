import React, { useState } from "react";
import {
  Link,
  NavLink,
  Outlet,
  useLocation,
  useNavigate,
} from "react-router-dom";

import { AiOutlineHome, AiOutlineSchedule } from "react-icons/ai";
import { GiNetworkBars } from "react-icons/gi";
import { FiSettings, FiUsers, FiNavigation, FiUserPlus } from "react-icons/fi";
import { FaUsers } from "react-icons/fa";
import { BiUserCircle } from "react-icons/bi";
import {
  MdOutlineComputer,
  MdOutlineRealEstateAgent,
  MdOutlineDataset,
} from "react-icons/md";

import { GrConfigure, GrHome, GrDocumentTransfer } from "react-icons/gr";
import { FaUsersRays, FaRegUser, FaUnity } from "react-icons/fa6";
import { TbWorldBolt } from "react-icons/tb";
import { IoMdTime, IoMdBusiness } from "react-icons/io";
import {
  MdAcUnit,
  MdGroup,
  MdOutlineQueue,
  MdCloseFullscreen,
} from "react-icons/md";
import { IoCodeWorking } from "react-icons/io5";

import { RiShutDownLine, RiTeamFill } from "react-icons/ri";
import RootNavClientName from "./RootNavClientName";
import { message } from "antd";
import axios from "axios";
import { API_BASE_URL, LOGOUT_API } from "../constant-API/constants";

function SlideBar() {
  const token = localStorage.getItem("token");
  const [userName, setUserName] = useState({
    username: "",
  });

  const roles = localStorage.getItem("roles");
  const navigate = useNavigate();
  const [isMenu, setisMenu] = useState(false);
  const [isResponsiveclose, setResponsiveclose] = useState(false);
  const toggleClass = () => {
    setisMenu(isMenu === false ? true : false);
    setResponsiveclose(isResponsiveclose === false ? true : false);
  };
  let boxClass = ["main-menu menu-right menuq1"];
  if (isMenu) {
    boxClass.push("menuq2");
  } else {
    boxClass.push("");
  }

  const [isMenuSubMenu, setMenuSubMenu] = useState(false);

  const toggleSubmenu = () => {
    setMenuSubMenu(isMenuSubMenu === false ? true : false);
  };

  //Conditionally rendering navbar

  const location = useLocation();
  const currentPath = location.pathname;
  const hideNavbarOnRoutes = [
    "/",
    "/forgot-password",
    "/profile",
    "/otp/:email",
    "/reset-password/:email/:token",
  ];
  //

  if (hideNavbarOnRoutes.includes(currentPath) || !token) {
    return null;
  }

  const dynamicSegmentPattern =
    /^\/(otp|reset-password|change-password|user-details)\/([^/]+)(\/Bearer%20[^/]+)?\/?$/;

  // Check if the current path matches the pattern
  const isDynamicSegmentPath = dynamicSegmentPattern.test(currentPath);

  // If the current path matches the dynamic segment pattern, do not render the Navbar
  if (isDynamicSegmentPath) {
    return null;
  }
  //

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("clientId");
    localStorage.removeItem("userId");

    axios
      .post(`${API_BASE_URL}${LOGOUT_API}`,
        {
          username: userName.username,
        },
        { headers: header }
      )
      .then((response) => {
        console.log("Logout successful:", response.data);
        setUserName(response.data);
        localStorage.removeItem("token");

        setTimeout(() => {
          message.success("Loggedout!");
        }, 100);
        navigate("/shiftwise");
      })

      .catch((error) => {
        console.log("Logout failed", error.response);

        setTimeout(() => {
          message.error("Logout failed");
        }, 500);
      });
  };

  //Show the CMS
  const IsSuperAdmin_Admin = localStorage.getItem("roles");
  const getRolesForAdmin_Super = ["ADMIN", "SUPERADMIN"].includes(
    IsSuperAdmin_Admin
  );

  //CMS components for ONLY ADMIN
  const getAdminRole = localStorage.getItem("roles");
  const IsAdmin = ["ADMIN"].includes(getAdminRole);

  return (
    <>
      <nav>
        <div className="container-fluid">
          <div className="row">
            <div className="col-2 min-vh-100" id="iconsColor">
              <ul className="list-unstyled" id="iconsbar">
                <div className="logo">
                  <h6 className=""> </h6>
                </div>
                <li className="">
                  <NavLink to="home" className="fs-2">
                    {" "}
                    <AiOutlineHome />{" "}
                    <span className="icons_font_size fs-6">Home</span>
                  </NavLink>
                </li>

                {/* CMS DropDown */}
                {getRolesForAdmin_Super && (
                  <>
                    <div className="dropdown">
                      <Link
                        to="#"
                        className="dropdown-toggle"
                        data-bs-toggle="dropdown"
                      >
                        <NavLink to="download" className="fs-2">
                          <FiNavigation />
                          <span className="icons_font_size fs-6">CMS</span>
                        </NavLink>
                      </Link>

                      <ul className="dropdown-menu">
                        <li className="dropdown-item">
                          <div className="dropend">
                            <span className="fs-4">
                              <FiNavigation />
                              <span className="icons_font_size fs-6">
                                Master
                              </span>
                              <span>→</span>
                            </span>
                          </div>
                          <ul
                            className="dropdown-menu "
                            style={{
                              marginLeft: "130px",
                              position: "absolute",
                              top: "0px",
                            }}
                          >
                            <li className="dropdown-item">
                              <NavLink
                                to="businessunit"
                                className="fs-4 submenu_icons"
                              >
                                <IoMdBusiness />
                                <span className="icons_font_size fs-6">
                                  Business Unit
                                </span>
                              </NavLink>
                            </li>
                            <li className="dropdown-item">
                              <NavLink
                                to="groups"
                                className="fs-4 submenu_icons"
                              >
                                <MdGroup />
                                <span className="icons_font_size fs-6">
                                  Groups
                                </span>
                              </NavLink>
                            </li>

                            <li className="dropdown-item">
                              <NavLink
                                to="processunit"
                                className="fs-4 submenu_icons"
                              >
                                <MdAcUnit />
                                <span className="icons_font_size fs-6">
                                  Process Unit
                                </span>
                              </NavLink>
                            </li>

                            <li className="dropdown-item">
                              <NavLink
                                to="queue"
                                className="fs-4 submenu_icons"
                              >
                                <MdOutlineQueue />
                                <span className="icons_font_size fs-6">
                                  Queue
                                </span>
                              </NavLink>
                            </li>

                            <li className="dropdown-item">
                              <NavLink
                                to="service"
                                className="fs-4 submenu_icons"
                              >
                                <MdCloseFullscreen />
                                <span className="icons_font_size fs-6">
                                  Service
                                </span>
                              </NavLink>
                            </li>

                            <li className="dropdown-item">
                              <NavLink
                                to="skill"
                                className="fs-4 submenu_icons"
                              >
                                <GrDocumentTransfer />
                                <span className="icons_font_size fs-6">
                                  Skill
                                </span>
                              </NavLink>
                            </li>

                            <li className="dropdown-item">
                              <NavLink
                                to="skillweightage"
                                className="fs-4 submenu_icons"
                              >
                                <GrConfigure />
                                <span className="icons_font_size fs-6">
                                  Skill Weightage
                                </span>
                              </NavLink>
                            </li>
                            <li className="dropdown-item">
                              <NavLink
                                to="subprocess"
                                className="fs-4 submenu_icons"
                              >
                                <FaUnity />
                                <span className="icons_font_size fs-6">
                                  Sub Process
                                </span>
                              </NavLink>
                            </li>
                            <li className="dropdown-item">
                              {" "}
                              <NavLink
                                to="system"
                                className="fs-4 submenu_icons"
                              >
                                <MdOutlineComputer />
                                <span className="icons_font_size fs-6">
                                  System
                                </span>
                              </NavLink>{" "}
                            </li>

                            <li className="dropdown-item">
                              <NavLink
                                to="teams"
                                className="fs-4 submenu_icons"
                              >
                                <RiTeamFill />
                                <span className="icons_font_size fs-6">
                                  Teams
                                </span>
                              </NavLink>
                            </li>

                            <li className="dropdown-item">
                              <NavLink
                                to="vendor"
                                className="fs-4 submenu_icons"
                              >
                                <MdOutlineDataset />
                                <span className="icons_font_size fs-6">
                                  Vendor
                                </span>
                              </NavLink>
                            </li>
                          </ul>
                        </li>

                        <li className="dropdown-item">
                          <NavLink
                            to="namespace"
                            className="fs-4 submenu_icons"
                          >
                            <FiUsers />
                            <span className="icons_font_size fs-6">
                              Namespace
                            </span>
                          </NavLink>
                        </li>
                        <li className="dropdown-item">
                          <NavLink
                            to="scheduler"
                            className="fs-4 submenu_icons"
                          >
                            <AiOutlineSchedule />
                            <span className="icons_font_size fs-6">
                              Scheduler
                            </span>
                          </NavLink>
                        </li>
                        <li className="dropdown-item">
                          <NavLink
                            to="systemconfig"
                            className="fs-4 submenu_icons"
                          >
                            <GrConfigure />
                            <span className="icons_font_size fs-6">
                              System Config
                            </span>
                          </NavLink>
                        </li>
                      </ul>
                    </div>
                  </>
                )}

                <li className="">
                  <NavLink to="roster" className="fs-2">
                    <GiNetworkBars />
                    <span className="icons_font_size fs-6">Roster</span>
                  </NavLink>
                </li>
                {/* user Settings */}
                <div className="dropdown">
                  <Link
                    to="settings"
                    className="dropdown-toggle"
                    data-bs-toggle="dropdown"
                  >
                    <NavLink to="download" className="fs-2 ">
                      <FiSettings />
                      <span className="icons_font_size fs-6">Settings</span>
                    </NavLink>
                  </Link>
                  <ul className="dropdown-menu">
                    <li className="dropdown-item">
                      <NavLink to="timezone" className="fs-4 submenu_icons">
                        <IoMdTime />
                        <span className="icons_font_size fs-6">Time Zone</span>
                      </NavLink>
                    </li>
                    <li className="dropdown-item">
                      <NavLink to="country" className="fs-4 submenu_icons">
                        <TbWorldBolt />
                        <span className="icons_font_size fs-6">Country</span>
                      </NavLink>
                    </li>
                    <li className="dropdown-item">
                      <NavLink to="state" className="fs-4 submenu_icons">
                        <MdOutlineRealEstateAgent />
                        <span className="icons_font_size fs-6">State</span>
                      </NavLink>
                    </li>
                    <li className="dropdown-item">
                      <NavLink to="holiday" className="fs-4 submenu_icons">
                        <GrHome />
                        <span className="icons_font_size fs-6">Holiday</span>
                      </NavLink>
                    </li>
                  </ul>
                </div>

                {/* user Module */}
                <div className="dropdown">
                  <Link
                    to="#"
                    className="dropdown-toggle"
                    data-bs-toggle="dropdown"
                  >
                    <NavLink to="download" className="fs-2">
                      <FaUsers />
                      <span className="icons_font_size fs-6">User Module</span>
                    </NavLink>
                  </Link>
                  <ul className="dropdown-menu">
                    <li className="dropdown-item">
                      <NavLink to="createuser" className="fs-4 submenu_icons">
                        <FiUserPlus />
                        <span className="icons_font_size fs-6">
                          Create User
                        </span>
                      </NavLink>
                    </li>
                    <li className="dropdown-item">
                      <NavLink to="createrole" className="fs-4 submenu_icons">
                        <IoCodeWorking />
                        <span className="icons_font_size fs-6">
                          Create Role
                        </span>
                      </NavLink>
                    </li>
                    {roles === "SUPERADMIN" ? (
                      <li className="dropdown-item">
                        <NavLink
                          to="createclient"
                          className="fs-4 submenu_icons"
                        >
                          <FaUsersRays />
                          <span className="icons_font_size fs-6">
                            Create Client
                          </span>
                        </NavLink>
                      </li>
                    ) : null}

                    {roles === "ADMIN" ? (
                      <li className="dropdown-item">
                        <NavLink to="client" className="fs-4 submenu_icons">
                          <FaUsersRays />
                          <span className="icons_font_size fs-6">
                            Client Details
                          </span>
                        </NavLink>
                      </li>
                    ) : null}
                  </ul>
                </div>

                {/* User Profile */}
                <div className="dropdown">
                  <Link
                    href="#"
                    className="dropdown-toggle"
                    data-bs-toggle="dropdown"
                  >
                    <NavLink to="download" className="fs-2">
                      <BiUserCircle />
                      <span className="icons_font_size fs-6">User Profile</span>
                    </NavLink>
                  </Link>
                  <ul className="dropdown-menu">
                    <li className="dropdown-item">
                      <NavLink to="profile" className="fs-5 submenu_icons">
                        <FaRegUser />
                        <span className="icons_font_size fs-6">Profile</span>
                      </NavLink>
                    </li>
                    <li
                      className="dropdown-item"
                      tabIndex="0"
                      onClick={handleLogout}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter' || e.key === ' ') {
                          handleLogout();
                        }
                      }}
                    >
                      <NavLink to="/shiftwise" className="fs-5 submenu_icons">
                        <RiShutDownLine />
                        <span className="icons_font_size fs-6">Logout</span>
                      </NavLink>
                    </li>
                  </ul>

                  <li className="d-flex align-items-end">
                    <div className="copyRight">Copyright ©2023 Exato.ai</div>
                  </li>
                </div>
              </ul>
              {/* <footer  className='text-center fs-5' style={{fontWeight:"600"}}>© 2023 Exato.ai</footer> */}
            </div>

            <div className="col-10 p-0 m-0 ">
              <RootNavClientName />
              <Outlet />
            </div>
          </div>
        </div>
      </nav>
    </>
  );
}

export default SlideBar;
