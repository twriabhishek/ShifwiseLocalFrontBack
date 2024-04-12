import {
  Button,
  Grid,
  IconButton,
  Paper,
  Table,
  TableContainer,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import CreateRoundedIcon from "@mui/icons-material/CreateRounded";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import AddCircleOutlineRoundedIcon from "@mui/icons-material/AddCircleOutlineRounded";
import DeleteForeverRoundedIcon from "@mui/icons-material/DeleteForeverRounded";
import { makeStyles } from "@mui/styles";
import axios from "axios";
import CreateClient from "./CreateClient";
import EditClient from "./EditClient";
import ConfirmationBox from "../createUser/ConfirmationBox";
import { message } from "antd";
import { FormCheck } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { ClipLoader } from "react-spinners";
import {
  API_BASE_URL,
  DELETE_CLIENT,
  GET_ALL_CLIENTS,
  GET_CLIENT_BY_CLIENTID,
  GET_ROLE_BY_ROLEID,
} from "../../../constant-API/constants";
import PageNavigation from "../../../page-navigation/PageNavigation";
import PageSize from "../../../page-navigation/PageSize";

const useStyles = makeStyles({
  customTableCell: {
    padding: "1px !important",
    paddingTop: "0px !important ",
    paddingBottom: "0px !important",
    wordBreak: "break-word !important",
  },
});

function ClientTable() {
  const classes = useStyles();

  const toggler = (id) => {
    //toggles active/inactive
  };
  const [activePage, setActivePage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [totalItems, setTotalItems] = useState(0);

  const [showClientModel, setShowClientModel] = useState(false);
  const [showEditClientModel, setShowEditClientModel] = useState(false);
  const [openModal, setOpenModal] = useState(false);
  const [deleteData, setDeleteData] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [clientData, setClientData] = useState({
    id: "id",
    clientId: "",
    clientName: "",
    spocName: "",
    email: "",
    password: "",
    phonenumber: "",
    bussinessnumber: "",
    address: "",
    assignedRoles: [],
    assignedRoleName: [],
    active: "",
  });
  const [data, setData] = useState([]);
  const navigate = useNavigate();
  const clientId = localStorage.getItem("clientId");
  const [loading, setLoading] = useState(true);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const generatePageSizeOptions = (dataLength) => {
    const options = [];
    for (let i = 5; i <= dataLength; i += 5) {
      options.push(i);
    }
    return options;
  };
  const fetchData = () => {
    const getAllClientsApi = `${API_BASE_URL}${GET_ALL_CLIENTS}`;
    axios
      .get(getAllClientsApi, { headers: header })
      .then((response) => {
        console.log("GET all clients successful", response.data);
        setData(response.data);
        setTotalItems(response.data.length);
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

  const startIndex = (activePage - 1) * itemsPerPage;

  const endIndex = startIndex + itemsPerPage;
  const displayedData = data.slice(startIndex, endIndex);
  const [pageSizeOptions, setPageSizeOptions] = useState([]);

  const handleItemsPerPageChange = (newItemsPerPage) => {
    setItemsPerPage(newItemsPerPage);
  };

  const handlePageChange = (pageNumber) => {
    setActivePage(pageNumber);
  };

  const handleClientUpdated = async (updateClient) => {
    const updateClientByIdApi = `${API_BASE_URL}${GET_ROLE_BY_ROLEID}/${clientId}`;
    const roleNames = await Promise.all(
      updateClient.assignedRoles.map(async (clientId) => {
        try {
          const response = await axios.get(updateClientByIdApi, {
            headers: header,
          });
          return response.data.name;
        } catch (error) {
          console.log("Error fetching role name", error);
          return "";
        }
      })
    );
    const updatedClient = { ...updateClient, assignedRoleName: roleNames };

    // Update the data state
    setData((prevData) =>
      prevData.map((item) =>
        item.clientId === updateClient.clientId ? updatedClient : item
      )
    );
  };

  const loadDetails = (clientId) => {
    navigate("/client-details/" + clientId);
  };

  const handleClientCreated = async (newClient) => {
    // Check if assignedRoles is defined before mapping
    if (newClient.assignedRoles && Array.isArray(newClient.assignedRoles)) {
      // Fetch the role name based on the assignedRoles ID
      const roleNames = await Promise.all(
        newClient.assignedRoles.map(async (id) => {
          try {
            const response = await axios.get(
              `${API_BASE_URL}/role/${id}`,
              { headers: header }
            );
            return response.data.name;
          } catch (error) {
            console.log("Error fetching role name", error);
            return "";
          }
        })
      );

      const updatedClient = { ...newClient, assignedRoleName: roleNames };

      setData([...data, updatedClient]);
    } else {
      console.error(
        "Assigned roles are missing or undefined in newClient",
        newClient
      );
    }
  };

  const handleEdit = (clientId) => {
    const clientByIdApi = `${API_BASE_URL}${GET_CLIENT_BY_CLIENTID}/${clientId}`;
    setShowEditClientModel(true);
    setClientData({
      id: "id",
      clientId: "",
      clientName: "",
      spocName: "",
      email: "",
      password: "",
      phonenumber: "",
      bussinessnumber: "",
      address: "",
      assignedRoles: [],
      assignedRoleName: [],
      active: "",
    });
    axios
      .get(clientByIdApi, { headers: header })
      .then((response) => {
        console.log("Get request by clientId successful", response.data);
        setClientData(response.data);

        console.log("Role data is", response.data);
      })
      .catch((error) => {
        console.log("Get request by id failed", error);
      });
  };
  const openDelete = (data) => {
    setOpenModal(true);
    setDeleteData(data);
  };
  const deleteUser = () => {
    const deleteClientApi = `${API_BASE_URL}${DELETE_CLIENT}/${deleteData?.clientId}`;
    setIsLoading(true);
    axios
      .delete(deleteClientApi, { headers: header })
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
  return (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <Paper>
          <div
            className="table-container"
            id="tableStartingStyling"
            style={{ margin: "0px" }}
          >
            <h4 className="text-center pt-3">Client Details</h4>
            <div className="d-flex justify-content-between align-items-center mb-1">
              <Button
                id="UserModuleBtn"
                className="buttonStyling"
                onClick={() => setShowClientModel(true)}
                startIcon={<AddCircleOutlineRoundedIcon fontSize="large" />}
              >
                Create client
              </Button>
              <PageSize
                handleItemsPerPageChange={handleItemsPerPageChange}
                itemsPerPage={itemsPerPage}
                pageSizeOptions={pageSizeOptions}
              />
            </div>

            <TableContainer
              component={Paper}
              // elevation={10}
              style={{ marginTop: "10px", minWidth: "100%" }}
            >
              <Table className={classes.smallTable} aria-label="simple table">
                <thead className="table-header">
                  <tr>
                    <th className="head">Sl no</th>
                    <th className="head" align="center">
                      Client Id
                    </th>
                    <th className="head" align="center">
                      Client name
                    </th>
                    <th className="head" align="center">
                      SpocName
                    </th>
                    <th className="head" align="center">
                      Email
                    </th>
                    <th className="head" align="center">
                      Phone Number{" "}
                    </th>
                    <th className="head" align="center">
                      Business Number
                    </th>
                    <th className="head" align="center">
                      Address
                    </th>
                    <th className="head" align="center">
                      {" "}
                      Assigned Roles
                    </th>
                    <th className="head" align="center">
                      Status
                    </th>
                    <th className="head" align="center">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="" id="ClientTable">
                  {data.length > 0 ? (
                    displayedData.map((row, index) => (
                      <tr className="table-body" key={row.clientId} id={index}>
                        <td>{startIndex + index + 1}</td>
                        <td>{row.clientId}</td>
                        <td align="center">{row.clientName}</td>
                        <td align="center">{row.spocName}</td>
                        <td align="center">{row.email}</td>
                        <td align="center">{row.phonenumber}</td>
                        <td align="center">{row.bussinessnumber}</td>
                        <td align="center">{row.address}</td>
                        <td align="center">
                          {Array.isArray(row.assignedRoleName)
                            ? row.assignedRoleName.join(", ")
                            : row.assignedRoleName}
                        </td>

                        <td
                          style={{ border: "1px solid black" }}
                          align="center"
                        >
                          <FormCheck
                            type="switch"
                            id={`switch-${row.clientId}`}
                            label=""
                            checked={row.active}
                            onChange={() => toggler(row.clientId)}
                          />
                        </td>
                        <td
                          style={{ border: "1px solid black" }}
                          align="center"
                          className="d-flex"
                        >
                          <IconButton
                            title="Edit"
                            className={classes.customTableCell}
                            onClick={() => {
                              handleEdit(row.clientId);
                            }}
                          >
                            <CreateRoundedIcon className="userModuleIcons">
                              <EditRoundedIcon
                                className={classes.customTableCell}
                              />
                            </CreateRoundedIcon>
                          </IconButton>

                          <IconButton
                            title="Delete"
                            onClick={(event) => {
                              openDelete(row);
                            }}
                          >
                            <DeleteForeverRoundedIcon className="userModuleIcons">
                              Delete
                            </DeleteForeverRoundedIcon>
                          </IconButton>
                          <Link to={`../client-details/${row.clientId}`}>
                            <IconButton>
                              <VisibilityIcon
                                className="userModuleIcons"
                                onClick={() => {
                                  loadDetails(row.clientId);
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
                          <span style={{ marginLeft: "8px" }}>Loading...</span>
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
            {/* </div>
      </div> */}

            {showClientModel && (
              <CreateClient
                showClientModel={showClientModel}
                setShowClientModel={setShowClientModel}
                handleClose={() => setShowClientModel(false)}
                handleClientCreated={handleClientCreated}
              />
            )}
            {showEditClientModel && (
              <EditClient
                showEditClientModel={showEditClientModel}
                setShowEditClientModel={setShowEditClientModel}
                handleClose={() => setShowEditClientModel(false)}
                clientData={clientData}
                handleClientUpdate={handleClientUpdated}
              />
            )}
            <ConfirmationBox
              openModal={openModal}
              closeDialog={() => setOpenModal(false)}
              title={deleteData?.spokeName}
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

export default ClientTable;
