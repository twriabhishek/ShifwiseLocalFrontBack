import { IconButton, Paper, Table, TableContainer } from "@mui/material";
import React, { useEffect, useState } from "react";
import CreateRoundedIcon from "@mui/icons-material/CreateRounded";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import { makeStyles } from "@mui/styles";
import axios from "axios";
import CreateClient from "./CreateClient";
import EditClient from "./EditClient";
import ConfirmationBox from "../createUser/ConfirmationBox";
import { FormCheck } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import VisibilityIcon from "@mui/icons-material/Visibility";
import { ClipLoader } from "react-spinners";
import {
  API_BASE_URL,
  GET_CLIENT_BY_CLIENTID,
} from "../../../constant-API/constants";

const useStyles = makeStyles({
  customTableCell: {
    padding: "1px !important",
    paddingTop: "0px !important ",
    paddingBottom: "0px !important",
    wordBreak: "break-word !important",
  },
});

function ClientDetails() {
  const classes = useStyles();

  const toggler = (id) => {};

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

  const fetchData = () => {
    const clientByIdApi = `${API_BASE_URL}${GET_CLIENT_BY_CLIENTID}/${clientId}`;
    axios
      .get(clientByIdApi, { headers: header })
      .then((response) => {
        console.log("GET all clients successful", response.data);

        setData((prev) => {
          return [...prev, response.data];
        });
      })
      .catch((err) => {
        console.log(err);
      });
  };
  useEffect(() => {
    fetchData();
  }, []);

  const handleClientUpdated = async (updateClient) => {
    const roleNames = await Promise.all(
      updateClient.assignedRoles.map(async (clientId) => {
        try {
          const response = await axios.get(
            `${API_BASE_URL}/role/${clientId}`,
            { headers: header }
          );
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

  return (
    <div
      className="table-container"
      id="tableStartingStyling"
      style={{ margin: "0px" }}
    >
      <h4 className="text-center pt-3">Client Details</h4>

      <TableContainer
        component={Paper}
        elevation={10}
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
              data.map((row, index) => (
                <tr className="table-body" key={row.clientId} id={index}>
                  <td>{index + 1}</td>
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

                  <td style={{ border: "1px solid black" }} align="center">
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
                        <EditRoundedIcon className={classes.customTableCell} />
                      </CreateRoundedIcon>
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
                    <ClipLoader color="#36D7B7" loading={loading} size={20} />
                  </>
                )}
              </div>
            )}
          </tbody>
        </Table>
      </TableContainer>

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
        isLoading={isLoading}
        setIsLoading={setIsLoading}
      />
    </div>
  );
}

export default ClientDetails;
