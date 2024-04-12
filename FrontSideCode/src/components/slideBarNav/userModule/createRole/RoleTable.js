import {
  Button,
  IconButton,
  Paper,
  Table,
  TableContainer,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import AddCircleOutlineRoundedIcon from "@mui/icons-material/AddCircleOutlineRounded";
import CreateRoundedIcon from "@mui/icons-material/CreateRounded";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import DeleteForeverRoundedIcon from "@mui/icons-material/DeleteForeverRounded";
import { makeStyles } from "@mui/styles";
import axios from "axios";
import CreateRole from "./CreateRole";
import EditRole from "./EditRole";
import { message } from "antd";
import ConfirmationBox from "../createUser/ConfirmationBox";
import { ClipLoader } from "react-spinners";
import {
  API_BASE_URL,
  DELETE_ROLE,
  GET_ALL_ROLES,
  GET_ROLE_BY_ROLEID,
} from "../../../constant-API/constants";

const useStyles = makeStyles({
  table: {
    minWidth: 650,
  },
});

function RoleTable() {
  const classes = useStyles();
  const [data, setData] = useState([]);
  const [showRoleModel, setShowRoleModel] = React.useState(false);
  const [showRoleEditModel, setShowRoleEditModel] = useState(false);
  const [openModal, setOpenModal] = useState(false);
  const [loading, setLoading] = useState(true);

  const [deleteData, setDeleteData] = useState([]);

  const [roleData, setRoleData] = useState({
    id: "id",
    name: "",
  });

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchData = () => {
    const getAllRoleApi = `${API_BASE_URL}${GET_ALL_ROLES}`;
    axios
      .get(getAllRoleApi, { headers: header })
      .then((response) => {
        console.log("GET all roles successful", response.data);
        setData(response.data);
      })
      .catch((err) => {
        console.log(err);
      });
  };
  useEffect(() => {
    fetchData();
  }, []);

  const handleRoleCreated = (newRole) => {
    setData([...data, newRole]);
  };

  const handleRoleUpdate = (updateRole) => {
    setData(
      data.map((item) => (item.id === updateRole.id ? updateRole : item))
    );
  };

  const handleRoleEdit = (id) => {
    const roleByIdApi = `${API_BASE_URL}${GET_ROLE_BY_ROLEID}/${id}`;
    setShowRoleEditModel(true);
    setRoleData({
      id: "id",
      name: "",
    });

    axios
      .get(roleByIdApi, { headers: header })
      .then((response) => {
        console.log("Get request by id successful", response.data);
        setRoleData(response.data);

        console.log("Role data is", response.data);
      })
      .catch((error) => {
        console.log("Get request by id failed", error);
      });
  };

  const roleDelete = (data) => {
    setOpenModal(true);
    setDeleteData(data);
  };

  const deleteUser = () => {
    const deleteRoleApi = `${API_BASE_URL}${DELETE_ROLE}/${deleteData?.id}`;
    axios
      .delete(deleteRoleApi, { headers: header })
      .then((response) => {
        console.log("Delete data successful", response.data);
        setTimeout(() => {
          message.success("Deleted successfully");
        }, 500);

        fetchData();

        setOpenModal(false);
      })
      .catch((err) => {
        console.log("Delete failed", err.response);
        setTimeout(() => {
          message.error("Delete failed");
        }, 500);
      });
  };
  return (
    <div className="table-container" style={{ margin: "0px" }}>
      <div className="row d-flex justify-content-center">
        <div className="col-11 ">
          <div className="container"></div>

          <h4 className="text-center pt-3">Role</h4>
          <Button
            id="UserModuleBtn"
            onClick={() => setShowRoleModel(true)}
            startIcon={<AddCircleOutlineRoundedIcon fontSize="large" />}
          >
            Create Role
          </Button>
          <TableContainer
            component={Paper}
            elevation={10}
            style={{ marginTop: "5px" }}
          >
            <Table className={classes.smallTable} aria-label="simple table">
              <thead className="table-header">
                <tr className="table-header">
                  <th className="head">Sl no</th>
                  <th align="center" className="head">
                    Id
                  </th>
                  <th align="center" className="head">
                    Name
                  </th>
                  <th align="center" className="head">
                    Action
                  </th>
                </tr>
              </thead>
              <tbody>
                {data.length > 0 ? (
                  data.map((row, index) => (
                    <tr className="table-body" key={row.id} id={index}>
                      <td>{index + 1}</td>
                      <td align="center" className="">
                        {row.id}
                      </td>
                      <td align="center">{row.name}</td>
                      <td
                        align="center"
                        className="d-flex justify-content-center"
                      >
                        <IconButton
                          title="Edit"
                          onClick={() => {
                            handleRoleEdit(row.id);
                          }}
                        >
                          <CreateRoundedIcon className="userModuleIcons">
                            <EditRoundedIcon />
                          </CreateRoundedIcon>
                        </IconButton>
                        <IconButton onClick={() => roleDelete(row)}>
                          <DeleteForeverRoundedIcon className="userModuleIcons">
                            Delete
                          </DeleteForeverRoundedIcon>
                        </IconButton>
                      </td>
                    </tr>
                  ))
                ) : (
                  // <p style={{ textAlign: "center" }}>No data available...</p>
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
        </div>
      </div>

      {showRoleModel && (
        <CreateRole
          showRoleModel={showRoleModel}
          setShowRoleModel={setShowRoleModel}
          handleClose={() => setShowRoleModel(false)}
          handleRoleCreated={handleRoleCreated}
        />
      )}
      {showRoleEditModel && (
        <EditRole
          showRoleEditModel={showRoleModel}
          setShowRoleModel={setShowRoleEditModel}
          handleClose={() => setShowRoleEditModel(false)}
          // handleEdit={handleEdit}
          roleData={roleData}
          handleRoleUpdate={handleRoleUpdate}
        />
      )}
      <ConfirmationBox
        openModal={openModal}
        closeDialog={() => setOpenModal(false)}
        title={deleteData?.name}
        deletefunction={deleteUser}
      />
    </div>
  );
}

export default RoleTable;
