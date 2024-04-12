import {
  Button,
  Grid,
  IconButton,
  Paper,
  Table,
  TableContainer,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import AddCircleOutlineRoundedIcon from "@mui/icons-material/AddCircleOutlineRounded";
import AddUser from "./AddUser";
import axios from "axios";
import { message } from "antd";
import CreateRoundedIcon from "@mui/icons-material/CreateRounded";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import { Link, useNavigate } from "react-router-dom";
import EditUser from "./EditUser";
import DeleteForeverRoundedIcon from "@mui/icons-material/DeleteForeverRounded";
import ConfirmationBox from "./ConfirmationBox";
import { ClipLoader } from "react-spinners";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { useTheme, useMediaQuery } from "@mui/material";
import {
  API_BASE_URL,
  DELETE_USER,
  GET_ALL_USER,
  GET_ROLE_BY_ROLEID,
  GET_USER_BY_ID,
} from "../../../constant-API/constants";
import PageNavigation from "../../../page-navigation/PageNavigation";
import PageSize from "../../../page-navigation/PageSize";
import { FormCheck } from "react-bootstrap";

const useStyles = makeStyles({
  customTableCell: {
    padding: "1px !important",
    paddingTop: "0px !important ",
    paddingBottom: "0px !important",
    wordBreak: "break-word !important",
  },

  hideOnSmall: {
    "@media (max-width: 600px)": {
      display: "none",
    },
  },
});

function UserTable({ roles }) {
  const [activePage, setActivePage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalItems, setTotalItems] = useState(0);
  console.log("total items", totalItems);
  const classes = useStyles();
  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down("sm"));

  const [data, setData] = useState([]);
  const [showEditModel, setShowEditModel] = useState(false);
  const [openModal, setOpenModal] = useState(false);
  const [deleteData, setDeleteData] = useState([]);

  const [showModel, setShowModel] = React.useState(false);
  const clientId = localStorage.getItem("clientId");
  console.log("ClientId is:", clientId);
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [loading, setLoading] = useState(true);

  const [userData, setUserData] = useState({
    id: "id",
    clientId: "",
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    assignedRoles: [],
    address: "",
    phonenumber: "",
    businessnumber: "",
    businessUnit: "",
    processUnit: "",
    team: "",
    group: "",
    active: "",
  });

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const handleEdit = (id) => {
    const api_Url = `${API_BASE_URL}${GET_USER_BY_ID}/${id}`;
    setShowEditModel(true);
    setUserData({
      id: "id",
      clientId: "",
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      assignedRoles: [],
      address: "",
      phonenumber: "",
      businessnumber: "",
      businessUnit: "",
      processUnit: "",
      team: "",
      group: "",
      active: "",
    });

    axios
      .get(api_Url, { headers: header })
      .then((response) => {
        console.log("Get request by id successful", response.data);
        setUserData(response.data);
      })
      .catch((error) => {
        console.log("Get request by id failed", error);
      });
  };

  const toggler = (id) => {};

  const startIndex = (activePage - 1) * itemsPerPage;
  console.log("start index", startIndex);
  const endIndex = startIndex + itemsPerPage;
  console.log("end index", endIndex);
  const [pageSizeOptions, setPageSizeOptions] = useState([]);
  const apiUrl = `${API_BASE_URL}${GET_ALL_USER}/${clientId}`;

  const generatePageSizeOptions = (dataLength) => {
    const options = [];
    for (let i = 5; i <= dataLength; i += 5) {
      options.push(i);
    }
    return options;
  };

  //Fetching data
  const fetchData = () => {
    axios
      .get(apiUrl, { headers: header })
      .then((response) => {
        console.log("GET all user successful", response.data);
        const apiStatus = response.data.active;
        console.log("api status", apiStatus);
        setData(response.data);
        setTotalItems(response.data.length);
        //Dynamically generate page size options based on the data length
        const dynamicPageSizeOptions = generatePageSizeOptions(
          response.data.length
        );
        setPageSizeOptions(dynamicPageSizeOptions);
      })
      .catch((err) => {
        console.log(err);
      });
  };
  useEffect(() => {
    fetchData();
  }, [activePage, itemsPerPage]);

  const displayedData = data.slice(startIndex, endIndex);

  const handleItemsPerPageChange = (newItemsPerPage) => {
    setItemsPerPage(newItemsPerPage);
  };

  //
  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleUserCreated = async (newUser) => {
    const assignedRoles = Array.isArray(newUser.assignedRoles)
      ? newUser.assignedRoles
      : [];
    // Fetch the role name based on the assignedRoles ID
    const roleNames = await Promise.all(
      assignedRoles.map(async (id) => {
        const roleApi = `${API_BASE_URL}${GET_ROLE_BY_ROLEID}/${id}`;
        try {
          const response = await axios.get(roleApi, { headers: header });
          return response.data.name;
        } catch (error) {
          console.log("Error fetching role name", error);
          return "";
        }
      })
    );
    const updatedClient = { ...newUser, assignedRoleName: roleNames };

    // Update the data state
    setData([...data, updatedClient]);
  };

  const handleUserUpdated = async (updateUser) => {
    // Fetch the role name based on the assignedRoles ID
    const roleNames = await Promise.all(
      updateUser.assignedRoles.map(async (id) => {
        const roleApi = `${API_BASE_URL}${GET_ROLE_BY_ROLEID}/${id}`;
        try {
          const response = await axios.get(roleApi, { headers: header });
          return response.data.name;
        } catch (error) {
          console.log("Error fetching role name", error);
          return "";
        }
      })
    );
    const updatedClient = { ...updateUser, assignedRoleName: roleNames };

    // Update the data state
    setData((prevData) =>
      prevData.map((item) => (item.id === updateUser.id ? updatedClient : item))
    );
  };

  //Delete
  function openDelete(data) {
    setOpenModal(true);
    setDeleteData(data);
  }

  const deleteUser = () => {
    const apiUrlDelete = `${API_BASE_URL}${DELETE_USER}/${deleteData?.id}`;
    setIsLoading(true);
    axios
      .delete(apiUrlDelete, { headers: header })
      .then((response) => {
        console.log("Delete data successful", response.data);
        setTimeout(() => {
          message.success("Deleted successfully");
        }, 500);
        fetchData();
        setOpenModal(false);
      })
      .catch((err) => {
        console.log(err);
        if (err.response && err.response.status === 500) {
          message.error(
            "An error occurred while deleting. Please try again later."
          );
        } else {
          message.error("Error deleting data.");
        }
      })
      .finally(() => {
        setIsLoading(false);
      });
  };
  //Details
  const loadDetails = (id) => {
    navigate("/user-details/" + id);
  };

  return (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <Paper>
          <div
            className={`table-container ${
              isSmallScreen ? classes.responsiveTable : ""
            }`}
            id="userModule"
            style={{ margin: "0px", overflowX: "auto" }}
          >
            <div className="table-responsive" id="tableStartingStyling">
              <div className="">
                <h4 className="text-center">User List</h4>
                <div className="d-flex justify-content-between align-items-center mb-1">
                  <Button
                    id="UserModuleBtn"
                    className="button_color userModuleAddButton"
                    onClick={() => setShowModel(true)}
                    startIcon={<AddCircleOutlineRoundedIcon fontSize="large" />}
                  >
                    Add User
                  </Button>

                  <PageSize
                    handleItemsPerPageChange={handleItemsPerPageChange}
                    itemsPerPage={itemsPerPage}
                    pageSizeOptions={pageSizeOptions}
                  />
                </div>
              </div>

              <TableContainer
                component={Paper}
                style={{ marginTop: "10px", minWidth: "100%" }}
              >
                <Table
                  className={`${classes.smallTable} ${
                    isSmallScreen ? classes.responsiveTable : ""
                  }`}
                  responsive
                >
                  <thead className="table-header">
                    <tr>
                      <th className="head">Sl no</th>
                      <th className="head">Id</th>
                      <th align="center" className="head">
                        Firstname
                      </th>
                      <th align="center" className="head">
                        Lastname
                      </th>
                      <th align="center" className="head">
                        Email
                      </th>
                      <th align="center" className="head">
                        Assigned Roles
                      </th>
                      <th align="center" className="head">
                        Address
                      </th>
                      <th align="center" className="head">
                        Phonenumber
                      </th>
                      <th align="center" className="head">
                        Businessnumber
                      </th>
                      <th align="center" className="head">
                        Businessunit
                      </th>
                      <th align="center" className="head">
                        Processunit
                      </th>
                      <th align="center" className="head">
                        Team
                      </th>
                      <th align="center" className="head">
                        Group
                      </th>
                      <th align="center" className="head">
                        Status
                      </th>
                      <th align="center" className="head">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.length > 0 ? (
                      displayedData.map((row, index) => (
                        <tr className="table-body" key={row.id} id={index}>
                          <td>{startIndex + index + 1}</td>
                          <td>{row.id}</td>
                          <td align="center">{row.firstName}</td>
                          <td align="center">{row.lastName}</td>
                          <td align="center">{row.email}</td>
                          <td align="center">
                            {Array.isArray(row.assignedRoleName)
                              ? row.assignedRoleName.join(", ")
                              : row.assignedRoleName}
                          </td>
                          <td align="center">{row.address}</td>
                          <td align="center">{row.phonenumber}</td>
                          <td align="center">{row.bussinessnumber}</td>
                          <td align="center">{row.businessUnit}</td>

                          <td align="center">{row.processUnit}</td>

                          <td align="center">{row.team}</td>
                          <td align="center">{row.group}</td>

                          <td align="center" className="switch-cell">
                            <FormCheck
                              type="switch"
                              id={`switch-${row.id}`}
                              label=""
                              color="success"
                              checked={row.active}
                              onChange={() => toggler(row.id)}
                            />
                          </td>
                          <td align="center" className="d-flex">
                            <IconButton
                              title="Edit"
                              onClick={() => {
                                handleEdit(row.id);
                              }}
                              className={classes.customTableCell}
                            >
                              <CreateRoundedIcon className="userModuleIcons">
                                <EditRoundedIcon />
                              </CreateRoundedIcon>
                            </IconButton>

                            <IconButton
                              onClick={() => openDelete(row)}
                              className={classes.customTableCell}
                            >
                              <DeleteForeverRoundedIcon className="userModuleIcons">
                                Delete
                              </DeleteForeverRoundedIcon>
                            </IconButton>
                            <Link to={`../user-details/${row.id}`}>
                              <IconButton>
                                <VisibilityIcon
                                  className="userModuleIcons"
                                  onClick={() => {
                                    loadDetails(row.id);
                                  }}
                                >
                                  View
                                </VisibilityIcon>
                              </IconButton>
                            </Link>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <div style={{ display: "flex", alignItems: "center" }}>
                        {loading && (
                          <>
                            <span style={{ marginLeft: "8px" }}>
                              Loading...
                            </span>
                            <ClipLoader
                              color="#36D7B7"
                              loading={loading}
                              size={20}
                            />
                          </>
                        )}
                      </div>
                    )}
                  </tbody>
                </Table>
              </TableContainer>
            </div>

            {showModel && (
              <AddUser
                showModel={showModel}
                setShowModel={setShowModel}
                handleClose={() => setShowModel(false)}
                handleUserCreated={handleUserCreated}
              />
            )}
            {showEditModel && (
              <EditUser
                showEditModel={showEditModel}
                setShowEditModel={setShowEditModel}
                handleClose={() => setShowEditModel(false)}
                userData={userData}
                handleUserCreated={handleUserCreated}
                handleUserUpdated={handleUserUpdated}
              />
            )}
            <ConfirmationBox
              openModal={openModal}
              closeDialog={() => setOpenModal(false)}
              title={deleteData?.name}
              deletefunction={deleteUser}
              isLoading={isLoading}
              setIsLoading={setIsLoading}
            />
          </div>
          <PageNavigation
            activePage={activePage}
            setActivePage={setActivePage}
            itemsPerPage={itemsPerPage}
            pageCount={Math.ceil(totalItems / itemsPerPage)}
            totalItems={totalItems}
            onPageChange={handlePageChange}
            pageSizeOptions={pageSizeOptions}
            handleItemsPerPageChange={handleItemsPerPageChange}
          />
        </Paper>
      </Grid>
    </Grid>
  );
}

export default UserTable;
