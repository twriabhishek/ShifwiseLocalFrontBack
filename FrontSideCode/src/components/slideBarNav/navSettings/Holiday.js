import React, { useState, useEffect } from "react";
import axios from "axios";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import { Modal } from "antd";
import Draggable from "react-draggable";
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
  const [editedRow, setEditedRow] = useState({});
  const [isAddMode, setIsAddMode] = useState(false);
  const openAddModal = () => {
    setIsAddMode(true);
    setIsModalVisible(true);
    setEditedRow({
      holidayType: "",
      holiday: "",
      description: "",
      date: "",
      geocode: "",
      location: "",
    });
  };
  const openEditModal = (index, row) => {
    setEditIndex(index);
    setEditedRow(row);
    setIsAddMode(false);
    setIsModalVisible(true);
  };
  const dummyTableData = [
    {
      holidayType: "Public",
      holiday: "New Year's Day",
      description: "Celebration of the new year",
      date: "2023-01-01",
      geocode: "XYZ",
      location: "City A",
    },
    {
        holidayType: "Public",
        holiday: "Independence Day",
        description: "Celebration of independence",
        date: "2023-07-04",
        geocode: "ABC",
        location: "City B",
      },
      {
        holidayType: "National",
        holiday: "Labor Day",
        description: "Honoring the contributions of workers",
        date: "2023-09-04",
        geocode: "DEF",
        location: "City C",
      },
      {
        holidayType: "Public",
        holiday: "Christmas Day",
        description: "Celebration of Christmas",
        date: "2023-12-25",
        geocode: "UVW",
        location: "City F",
      },
      {
        holidayType: "Public",
        holiday: "Thanksgiving Day",
        description: "Thanksgiving celebration",
        date: "2023-11-23",
        geocode: "LMN",
        location: "City D",
      },
      {
        holidayType: "National",
        holiday: "Memorial Day",
        description: "Honoring military personnel",
        date: "2023-05-29",
        geocode: "PQR",
        location: "City E",
      },

  ];
  

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
        "http://shiftwise-f79dc988f3c07bfb.elb.us-east-1.amazonaws.com:8081/cmsmodule/teams", 
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
        "http://shiftwise-f79dc988f3c07bfb.elb.us-east-1.amazonaws.com:8081/cmsmodule/teams", 
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
          "http://shiftwise-f79dc988f3c07bfb.elb.us-east-1.amazonaws.com:8081/cmsmodule/teams",
          jsonData,
          { headers: header }
        );

        if (response.status === 201) {
          setTeamNames([...teamNames, teamName]);
          setTeamName("");
          setErrorText("");
          console.log("Team Name successfully added:", teamName);
        } else {
          setErrorText("Error adding team name. Please try again.");
          console.error("Error adding team name:", response.statusText);
        }
      } catch (error) {
        setErrorText("Network error. Please try again.");
        console.error("Network error:", error);
      }
    }
    fetchDataForTable();
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
  const handleUpdates = (index, row) => {
    setEditIndex(index);
    setEditedRow(row);
  
    if (tableData[index]) {
      setIsModalVisible(true);
    } else {
      console.error("Invalid index or data for editing.");
    }
  };
  
  const handleDelete = async (index, teamId) => {
    try {
      const response = await axios.delete(
        `http://shiftwise-f79dc988f3c07bfb.elb.us-east-1.amazonaws.com:8081/cmsmodule/teams/${teamId}`, // Update the API endpoint
        { headers: header }
      );
  
      if (response.status === 202) {
        // Team deleted successfully from the API
        const updatedData = [...tableData];
        updatedData.splice(index, 1);
        setTableData(updatedData);
  
        const updatedNames = [...teamNames];
        updatedNames.splice(index, 1);
        setTeamNames(updatedNames);
  
        setErrorText("");
      } else {
        setErrorText("Error deleting team. Please try again.");
        console.error("Error deleting team:", response.statusText);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };
  
  const handleSave = async (index) => {
    const updatedData = [...tableData];
    const updatedTeam = updatedData[index];
    const { teamId, clientId, ...rest } = updatedTeam;
  
    const updatedJsonData = {
      ...rest,
      teamName: editedName,
      clientId: clientId, // Make sure to include clientId in the updated data
    };
  
    try {
      const response = await axios.put(
        `http://shiftwise-f79dc988f3c07bfb.elb.us-east-1.amazonaws.com:8081/cmsmodule/teams/${teamId}`, // Update the API endpoint
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

      const response = await axios.put(
        `http://shiftwise-f79dc988f3c07bfb.elb.us-east-1.amazonaws.com:8081/cmsmodule/teams/${teamId}`,
        updatedJsonData,
        { headers: header }
      );

      if (response.status === 200) {
        updatedData[editIndex] = { ...updatedTeam, teamName: editedName };
        setTableData(updatedData);
        setEditingRow(-1);
        setEditIndex(-1);
        setIsModalVisible(false);
        setErrorText("");
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
      <h4 className="text-center pt-3">Holiday Configuration</h4>
      
      <form onSubmit={handleSubmit} className="">
        <div className="row d-flex justify-content-center">
          <div className="col-12 col-md-4 col-lg-4">
           
            
            {inputWarning && <p className="warning">{inputWarning}</p>}
            <div className="d-flex justify-content-center">
              <button className="btn buttonStyling" type="button" onClick={openAddModal}>
                Add Holiday 
              </button>
            </div>
          </div>
        </div>
      </form>

      <div className="row d-flex justify-content-center">
        <div className="col-11">
          {dummyTableData.length > 0 && (
            <div className="table_overflow">
              <table className="">
                <thead className="table_heading">
                  <tr>
                    {Object.keys(dummyTableData[0]).map((column, index) => (
                      <th key={index}>{column}</th>
                    ))}
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {dummyTableData.map((row, rowIndex) => (
                    <tr key={rowIndex}>
                      {Object.values(row).map((value, columnIndex) => (
                        <td key={columnIndex}>
                          {editingRow === rowIndex ? (
                            <input
                              type="text"
                              value={editedName}
                              onChange={(e) =>
                                setEditedName(e.target.value)
                              }
                              className="editing-input namespaceIcons"
                            />
                          ) : (
                            value
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
                                  onClick={() => handleUpdates(rowIndex, row)}
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
      <Modal
        title={
          <div
            style={{ cursor: "move" }}
            onMouseOver={() => setDisabled(false)}
            onMouseOut={() => setDisabled(!inputFocus)}
          >
            {isAddMode ? "Add Holiday" : "Edit Holiday"}
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
<label htmlFor="editedName">Holiday:</label>
<input
  type="text"
  id="editedName"
  name="editedName"
  value={editedRow.holiday}
  onChange={(e) => setEditedRow({ ...editedRow, holiday: e.target.value })}
  className="form-control"
/>

        <label htmlFor="editedHolidayType">Holiday Type:</label>
  <input
    type="text"
    id="editedHolidayType"
    name="editedHolidayType"
    value={editedRow.holidayType}
    onChange={(e) => setEditedRow({ ...editedRow, holidayType: e.target.value })}
    className="form-control"
  />


  <label htmlFor="editedDescription">Description:</label>
  <input
    type="text"
    id="editedDescription"
    name="editedDescription"
    value={editedRow.description}
    onChange={(e) => setEditedRow({ ...editedRow, description: e.target.value })}
    className="form-control"
  />
   <label htmlFor="editedDate">Date:</label>
  <input
    type="text"
    id="editedDate"
    name="editedDate"
    value={editedRow.date}
    onChange={(e) => setEditedRow({ ...editedRow, date: e.target.value })}
    className="form-control"
  />

  <label htmlFor="editedGeocode">Geocode:</label>
  <input
    type="text"
    id="editedGeocode"
    name="editedGeocode"
    value={editedRow.geocode}
    onChange={(e) => setEditedRow({ ...editedRow, geocode: e.target.value })}
    className="form-control"
  />

  <label htmlFor="editedLocation">Location:</label>
  <input
    type="text"
    id="editedLocation"
    name="editedLocation"
    value={editedRow.location}
    onChange={(e) => setEditedRow({ ...editedRow, location: e.target.value })}
    className="form-control"
  />
        <div style={{ textAlign: "center", marginTop: "20px" }}>
        <button
            className="btn btn-save-changes"
            onClick={isAddMode ? handleSubmit : handleModalSave}
          >
            {isAddMode ? "Add Holiday" : "Save Changes"}
          </button>
        </div>
      </Modal>
    </div>
  );
}

export default Team;
