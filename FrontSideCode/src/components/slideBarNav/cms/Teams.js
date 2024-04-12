import React, { useState, useEffect } from "react";
import axios from "axios";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import { Modal } from "antd";
import Draggable from "react-draggable";
import { Snackbar } from "@mui/material";
import Alert from "@mui/material/Alert";
import { API_BASE_URL, TEAMS } from "../../constant-API/constants";
function Team() {
  const [teamName, setTeamName] = useState("");
  const [teamNames, setTeamNames] = useState([]);
  const [inputWarning, setInputWarning] = useState("");
  const [editIndex, setEditIndex] = useState(-1);
  const [editedName, setEditedName] = useState("");
  const [errorText, setErrorText] = useState("");
  const [tableData, setTableData] = useState([]);
  const [tableColumns, setTableColumns] = useState([]);
  const [editingRow, setEditingRow] = useState(-1);
  const [showModal, setShowModal] = useState(false);
  const [editedItem, setEditedItem] = useState({});
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [dragging, setDragging] = useState(false);
  const [disabled, setDisabled] = useState(true);
  const [inputFocus, setInputFocus] = useState(false)
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
  const [popupSnackbarOpen, setPopupSnackbarOpen] = useState(false);
  const [popupSnackbarMessage, setPopupSnackbarMessage] = useState("");
  const [popupSnackbarSeverity, setPopupSnackbarSeverity] = useState("success");



  useEffect(() => {
    fetchDataForTable();
    fetchExistingTeamNames();
  }, []);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchDataForTable = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}${TEAMS}`,
        { headers: header }
      );

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const columns = Object.keys(data[0]).filter(
            (column) => column !== "teamId" && column !== "clientId"
          );
          setTableColumns(columns);
        }
        setTableData(data);
        setErrorText("");
      } else {
        setErrorText("Error fetching data from the API.");
        console.error("Error fetching data:", response.statusText);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };

  const fetchExistingTeamNames = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}${TEAMS}`,
        { headers: header }
      );

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const names = data.map((item) => item.teamName);
          setTeamNames(names);
        }
      } else {
        setErrorText("Error fetching existing team names.");
        console.error("Error fetching existing team names:", response.statusText);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (teamName.trim() !== "") {
      const jsonData = { teamName: teamName };

      try {
        const response = await axios.post(
          `${API_BASE_URL}${TEAMS}`,
          jsonData,
          { headers: header }
        );

        if (response.status === 201) {
          setTeamNames([...teamNames, teamName]);
          setTeamName("");
          setErrorText("");
          setSnackbarMessage("Team Name successfully added");
          setSnackbarSeverity("success");
          setSnackbarOpen(true);
          console.log("Team Name successfully added:", teamName);
        } else {
          setErrorText("Error adding team name. Please try again.");
          setSnackbarMessage(
            "Error adding team name. Please try again."
          );
          setSnackbarSeverity("error");
          setSnackbarOpen(true);
          console.error("Error adding team name:", response.statusText);
        }
      } catch (error) {
        setErrorText("Network error. Please try again.");
        setSnackbarMessage("Network error. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
        console.error("Network error:", error);
      }
    }
    fetchDataForTable();
  };
  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };
  const handleInputChange = (e) => {
    const inputValue = e.target.value;

    // Add your validation logic if needed
    setTeamName(inputValue);

    // Add your validation warning logic if needed
    if (inputValue.trim() !== "") {
      setInputWarning("");
    } else {
      setInputWarning("Team name cannot be empty.");
    }
  };

  const handleUpdates = (index, name) => {
    setEditIndex(index);
    setEditedName(name);

    if (tableData[index]) {
      setEditedItem(tableData[index]);
      setIsModalVisible(true);
    } else {
      console.error("Invalid index or data for editing.");
    }
  };

  // Delete Teams
  const handleDelete = async (index, teamId) => {
    try {
      const response = await axios.delete(
        `${API_BASE_URL}${TEAMS}/${teamId}`, // Update the API endpoint
        { headers: header }
      );

      if (response.status === 200) {
        // Team deleted successfully from the API
        const updatedData = [...tableData];
        updatedData.splice(index, 1);
        setTableData(updatedData);

        const updatedNames = [...teamNames];
        updatedNames.splice(index, 1);
        setTeamNames(updatedNames);

        setErrorText("");
        setSnackbarMessage("Team successfully deleted");
        setSnackbarSeverity("success");
        setSnackbarOpen(true);
      } else {
        setErrorText("Error deleting team. Please try again.");
        console.error("Error deleting team:", response.statusText);
        setSnackbarMessage("Error deleting Team. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      setSnackbarMessage("Network error. Please try again.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
      console.error("Network error:", error);
    }
  };

  const handleSave = async (index) => {
    const updatedData = [...tableData];
    const updatedTeam = updatedData[index];
    const { teamId, clientId, ...rest } = updatedTeam;

    const updatedJsonData = {
      ...rest, teamName: editedName, clientId: clientId, // Make sure to include clientId in the updated data
    };

    try {
      const response = await axios.put(
        `${API_BASE_URL}${TEAMS}/${teamId}`, // Update the API endpoint
        updatedJsonData,
        { headers: header }
      );

      if (response.status === 200) {
        // Update the local state with the edited name
        updatedData[index] = { ...updatedTeam, teamName: editedName };
        setTableData(updatedData);
        setEditingRow(-1); // Exit edit mode
        setEditIndex(-1);
        setErrorText(""); // Reset error text on successful update
      } else {
        setErrorText("Error updating team. Please try again.");
        console.error("Error updating team:", response.statusText);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };
  const handleEditClick = (index, name) => {
    setEditIndex(index);
    setEditedName(name);

    if (tableData[index]) {
      setEditedItem(tableData[index]);
      setShowModal(true);
    } else {
      console.error("Invalid index or data for editing.");
    }
  };
  const handleCloseModal = () => {
    setShowModal(false);
  };


  // Update Team Model
  const handleModalSave = async () => {
    try {
      const updatedData = [...tableData];
      const updatedTeam = updatedData[editIndex];
      const { teamId, clientId, ...rest } = updatedTeam;

      const updatedJsonData = {
        ...rest,
        teamName: editedName,
        clientId: clientId, // Make sure to include clientId in the updated data
      };
      console.log(`${API_BASE_URL}${TEAMS}/${teamId}`, updatedJsonData,);
      const response = await axios.put(`${API_BASE_URL}${TEAMS}/${teamId}`, updatedJsonData, { headers: header });
      console.log('response', response);
      if (response.status === 200) {
        updatedData[editIndex] = { ...updatedTeam, teamName: editedName };
        setTableData(updatedData);
        setEditingRow(-1);
        setEditIndex(-1);
        setIsModalVisible(false);
        setErrorText("");
        setPopupSnackbarMessage("Team successfully updated");
        setPopupSnackbarSeverity("success");
        setPopupSnackbarOpen(true);
      } else {
        setErrorText("Error updating team. Please try again.");
        console.error("Error updating team:", response.statusText);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };
  const handleCancels = () => {
    setIsModalVisible(false);
  };

  const onStart = () => {
    setDragging(true);
  };

  const onStop = () => {
    setDragging(false);
  };

  const handleInputFocus = () => {
    setInputFocus(true);
  };

  const handleInputBlur = () => {
    setInputFocus(false);
  };


  return (
    <div>
      <h4 className="text-center pt-3">Team Creation</h4>

      <form onSubmit={handleSubmit} className="">
        <div className="row d-flex justify-content-center">
          <div className="col-12 col-md-4 col-lg-4">
            <input
              type="text"
              id="teamName"
              name="teamName"
              required
              value={teamName}
              onChange={handleInputChange}
              className="form-control"
              placeholder="Team Name"
            />

            {inputWarning && <p className="warning">{inputWarning}</p>}
            <div className="d-flex justify-content-center">
              <button className="btn buttonStyling" type="submit">
                Submit
              </button>
            </div>
          </div>
        </div>
      </form>

      <div className="row d-flex justify-content-center">
        <div className="col-11">
          {teamNames.length > 0 && (
            <div className="table_overflow">
              <table className="">
                <thead className="table_heading">
                  <tr>
                    <th>Serial Number</th>
                    {tableColumns.map((column, index) => (
                      <th key={index}>{column}</th>
                    ))}
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {tableData.map((row, rowIndex) => (
                    <tr key={rowIndex}>
                      <td>{rowIndex + 1}</td>
                      {tableColumns.map((column, columnIndex) => (
                        <td key={columnIndex}>
                          {editingRow === rowIndex && column === "teamName" ? (
                            <input
                              type="text"
                              value={editedName}
                              onChange={(e) =>
                                setEditedName(e.target.value)
                              }
                              className="editing-input namespaceIcons"
                            />
                          ) : (
                            row[column]
                          )}
                        </td>
                      ))}
                      <td>
                        {editingRow !== rowIndex ? (
                          <>
                            <div className="d-flex justify-content-center align-items-center">
                              <div className="EditDeleteBtnStyling m-auto mx-1">
                                <EditTwoToneIcon
                                  className="edit- text-center myEditIcons"
                                  onClick={() => handleUpdates(rowIndex, row.teamName)}
                                />
                              </div>
                              <div className="EditDeleteBtnStyling m-auto mx-1">
                                <DeleteTwoToneIcon
                                  className="edit- text-center myEditIcons"
                                  onClick={() => handleDelete(rowIndex, row.teamId)}
                                />
                              </div>
                            </div>
                          </>
                        ) : (
                          <button
                            className="SystemUpdateButton"
                            onClick={() => handleSave(rowIndex)}
                          >
                            Save
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        style={{ top: "80px" }}
      >
        <Alert onClose={handleSnackbarClose} severity={snackbarSeverity}>
          {snackbarMessage}
        </Alert>
      </Snackbar>

      <Snackbar
        open={popupSnackbarOpen}
        autoHideDuration={6000}
        onClose={() => setPopupSnackbarOpen(false)}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        style={{ top: "80px" }}
      >
        <Alert
          onClose={() => setPopupSnackbarOpen(false)}
          severity={popupSnackbarSeverity}
        >
          {popupSnackbarMessage}
        </Alert>
      </Snackbar>
      <Modal
        title={
          <div
            style={{ cursor: "move" }}
            onMouseOver={() => setDisabled(false)}
            onMouseOut={() => setDisabled(!inputFocus)}
          >
            Edit Teams
          </div>
        }
        open={isModalVisible}
        onCancel={handleCancels}
        footer={null}
        modalRender={(modal) => (
          <Draggable
            disabled={dragging || inputFocus}
            onStart={onStart}
            onStop={onStop}
          >
            <div>{modal}</div>
          </Draggable>
        )}
      >
        <label htmlFor="editedName">Teams Name:</label>
        <input
          type="text"
          id="editedName"
          name="editedName"
          value={editedName}
          onChange={(e) => setEditedName(e.target.value)}
          onFocus={handleInputFocus}
          onBlur={handleInputBlur}
          className="form-control"
        />
        <div style={{ textAlign: "center", marginTop: "20px" }}>
          <button className="btn btn-save-changes" onClick={handleModalSave}>
            Save Changes
          </button>
        </div>
      </Modal>
    </div>
  );
}

export default Team;
