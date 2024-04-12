import React, { useEffect, useState } from "react";
import { Routes, Route, Navigate, Outlet, useNavigate } from "react-router-dom";
import "./App.css";

import Home from "./components/slideBarNav/Home/Home";
import About from "./components/About";
import Timezone from "./components/slideBarNav/navSettings/Timezone";
import Country from "./components/slideBarNav/navSettings/Country";
import State from "./components/slideBarNav/navSettings/State";
import Holiday from "./components/slideBarNav/navSettings/Holiday";
import Logout from "./components/slideBarNav/userProfile/Logout";
import Profile from "./components/slideBarNav/userProfile/Profile";
import Login from "./components/login/Login";
import System from "./components/slideBarNav/cms/System";
import Vendor from "./components/slideBarNav/cms/VendorCMS/Vendor";
import Namespace from "./components/slideBarNav/cms/Namespace";
import Systemconfig from "./components/slideBarNav/cms/Systemconfig";
import UserTable from "./components/slideBarNav/userModule/createUser/UserTable";
import RoleTable from "./components/slideBarNav/userModule/createRole/RoleTable";
import ClientTable from "./components/slideBarNav/userModule/createClient/ClientTable";
import RosterTable from "./components/slideBarNav/roaster/RosterTable";
import ForgotPassword from "./components/forgotpassword/ForgotPassword";
import VerifyOTP from "./components/forgotpassword/VerifyOTP";
import ResetPassword from "./components/forgotpassword/ResetPassword";
import ChangePassword from "./components/forgotpassword/ChangePassword";
import SlideBar from "./components/slideBarNav/SlideBar";
import CreateRole from "./components/slideBarNav/userModule/createRole/CreateRole";
import ViewUser from "./components/slideBarNav/userModule/createUser/ViewUser";
import axios from "axios";
import { AuthorizedUser } from "./components/privateroute/PrivateRoute";
import BusinessUnit from "./components/slideBarNav/cms/BusinessUnit";
import ProcessUnit from "./components/slideBarNav/cms/ProcessUnit";
import SubProcess from "./components/slideBarNav/cms/SubProcess";
import Team from "./components/slideBarNav/cms/Teams";
import Group from "./components/slideBarNav/cms/Groups";
import Queue from "./components/slideBarNav/cms/Queue";
import Skill from "./components/slideBarNav/cms/Skill";
import SkillWeightage from "./components/slideBarNav/cms/SkillWeightage";
import Scheduler from "./components/slideBarNav/cms/schedulerSection/Scheduler";
import ViewClient from "./components/slideBarNav/userModule/createClient/ViewClient";
import ServiceAdd from "./components/slideBarNav/servicesection/ServiceAdd";
import PageNotFound from "./components/PageNotFound";
import ClientDetails from "./components/slideBarNav/userModule/createClient/ClientDetails";

function App() {
  const clientId = localStorage.getItem("clientId");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isForgot, setIsForgot] = useState(false);
  const [profileData, setProfileData] = useState({});

  const handleLogin = () => {
    setIsLoggedIn(true);
  };

  return (
    <div>
      <Routes>
        <Route
          path="/shiftwise"
          element={
            isLoggedIn ? (
              <Navigate to="/shiftwise/slide/home" />
            ) : (
              <Login onLogin={handleLogin} />
            )
          }
        ></Route>

        <>
          <Route
            path="/shiftwise/slide/"
            element={
              <AuthorizedUser>
                <SlideBar />
              </AuthorizedUser>
            }
          >
            <Route
              path="*"
              element={
                <AuthorizedUser>
                  <PageNotFound />
                </AuthorizedUser>
              }
            />

            <Route
              path="home"
              element={
                <AuthorizedUser>
                  <Home />
                </AuthorizedUser>
              }
            />
            <Route
              path="roster"
              element={
                <AuthorizedUser>
                  <RosterTable />
                </AuthorizedUser>
              }
            />
            <Route
              path="country"
              element={
                <AuthorizedUser>
                  <Country />
                </AuthorizedUser>
              }
            />
            <Route
              path="state"
              element={
                <AuthorizedUser>
                  <State />
                </AuthorizedUser>
              }
            />
            <Route
              path="timezone"
              element={
                <AuthorizedUser>
                  <Timezone />
                </AuthorizedUser>
              }
            />
            <Route
              path="holiday"
              element={
                <AuthorizedUser>
                  <Holiday />
                </AuthorizedUser>
              }
            />

            <Route
              path="system"
              element={
                <AuthorizedUser>
                  <System />
                </AuthorizedUser>
              }
            />
            <Route
              path="vendor"
              element={
                <AuthorizedUser>
                  <Vendor />
                </AuthorizedUser>
              }
            />
            <Route
              path="namespace"
              element={
                <AuthorizedUser>
                  <Namespace />
                </AuthorizedUser>
              }
            />
            <Route
              path="systemconfig"
              element={
                <AuthorizedUser>
                  <Systemconfig />
                </AuthorizedUser>
              }
            />
            <Route
              path="businessunit"
              element={
                <AuthorizedUser>
                  <BusinessUnit />
                </AuthorizedUser>
              }
            />
            <Route
              path="processunit"
              element={
                <AuthorizedUser>
                  <ProcessUnit />
                </AuthorizedUser>
              }
            />
            <Route
              path="subprocess"
              element={
                <AuthorizedUser>
                  <SubProcess />
                </AuthorizedUser>
              }
            />
            <Route
              path="teams"
              element={
                <AuthorizedUser>
                  <Team />
                </AuthorizedUser>
              }
            />
            <Route
              path="groups"
              element={
                <AuthorizedUser>
                  <Group />
                </AuthorizedUser>
              }
            />
            <Route
              path="queue"
              element={
                <AuthorizedUser>
                  <Queue />
                </AuthorizedUser>
              }
            />
            <Route
              path="skill"
              element={
                <AuthorizedUser>
                  <Skill />
                </AuthorizedUser>
              }
            />
            <Route
              path="skillweightage"
              element={
                <AuthorizedUser>
                  <SkillWeightage />
                </AuthorizedUser>
              }
            />
            <Route
              path="scheduler"
              element={
                <AuthorizedUser>
                  <Scheduler />
                </AuthorizedUser>
              }
            />

            <Route
              path="createuser"
              element={
                <AuthorizedUser>
                  {" "}
                  <UserTable />{" "}
                </AuthorizedUser>
              }
            ></Route>
            <Route
              path="user-details/:id"
              element={
                <AuthorizedUser>
                  <ViewUser />
                </AuthorizedUser>
              }
            />
            <Route
              path="createrole"
              element={
                <AuthorizedUser>
                  <RoleTable />
                </AuthorizedUser>
              }
            />
            <Route
              path="createclient"
              element={
                <AuthorizedUser>
                  <ClientTable />
                </AuthorizedUser>
              }
            />
            <Route
              path="client-details/:clientId"
              element={
                <AuthorizedUser>
                  <ViewClient />
                </AuthorizedUser>
              }
            />
            <Route
              path="client"
              element={
                <AuthorizedUser>
                  <ClientDetails />
                </AuthorizedUser>
              }
            />
            <Route
              path="service"
              element={
                <AuthorizedUser>
                  <ServiceAdd />
                </AuthorizedUser>
              }
            />

            <Route
              path="profile"
              element={
                <AuthorizedUser>
                  <Profile clientId={clientId} />
                </AuthorizedUser>
              }
            />
          </Route>
        </>
      </Routes>
      <Routes>
        <Route path="/shiftwise/logout" element={<Logout />} />

        <Route path="/shiftwise/forgot-password" element={<ForgotPassword />} />
        <Route exact path="/shiftwise/otp/:email" element={<VerifyOTP />} />
        <Route exact path="/shiftwise/reset-password/:email/:token" element={<ResetPassword />} />
        <Route exact path="/shiftwise/change-password/:email" element={<ChangePassword />} />
      </Routes>
    </div>
  );
}

export default App;
