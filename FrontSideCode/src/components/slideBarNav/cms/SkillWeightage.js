import React, { useState, useEffect } from "react";
import axios from "axios";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import { Modal } from "antd";
import Draggable from "react-draggable";
import { Snackbar } from "@mui/material";
import Alert from "@mui/material/Alert";
import { API_BASE_URL, SKILL_WEIGHTAGES } from "../../constant-API/constants";

function SkillWeightage() {
  const [weightageName, setWeightageName] = useState("");
  const [weightageNames, setWeightageNames] = useState([]);
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
    fetchExistingWeightageNames();
  }, []);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchDataForTable = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}${SKILL_WEIGHTAGES}`, 
        { headers: header }
      );

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const columns = Object.keys(data[0]).filter(
            (column) => column !== "skillWeightageId" && column !== "clientId"
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

  const fetchExistingWeightageNames = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}${SKILL_WEIGHTAGES}`, 
        { headers: header }
      );

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const names = data.map((item) => item.skillWeightageName);
          setWeightageNames(names);
        }
      } else {
        setErrorText("Error fetching existing weightage names.");
        console.error("Error fetching existing weightage names:", response.statusText);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };
  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (weightageName.trim() !== "") {
      const jsonData = { skillWeightageName: weightageName };

      try {
        const response = await axios.post(
          `${API_BASE_URL}${SKILL_WEIGHTAGES}`, // Update the API endpoint
          jsonData,
          { headers: header }
        );

        if (response.status === 201) {
          setWeightageNames([...weightageNames, weightageName]);
          setWeightageName("");
          setErrorText("");
          setSnackbarMessage("Skill Weightage Name successfully added");
          setSnackbarSeverity("success");
          setSnackbarOpen(true);
        } else {
          setErrorText("Error adding weightage name. Please try again.");
          setSnackbarMessage(
            "Error adding Skill Weightage name. Please try again."
          );
          setSnackbarSeverity("error");
          setSnackbarOpen(true);
          console.error("Error adding weightage name:", response.statusText);
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
    setWeightageName(inputValue);
  
    // Add your validation warning logic if needed
    if (inputValue.trim() !== "") {
      setInputWarning("");
    } else {
      setInputWarning("Weightage name cannot be empty.");
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
  
  const handleDelete = async (index, weightageId) => {
    try {
      const response = await axios.delete(
        `${API_BASE_URL}${SKILL_WEIGHTAGES}/${weightageId}`, // Update the API endpoint
        { headers: header }
      );
  
      if (response.status === 202) {
        // Weightage deleted successfully from the API
        const updatedData = [...tableData];
        updatedData.splice(index, 1);
        setTableData(updatedData);
  
        const updatedNames = [...weightageNames];
        updatedNames.splice(index, 1);
        setWeightageNames(updatedNames);
  
        setErrorText("");
        setSnackbarMessage("Skill Weightage successfully deleted");
        setSnackbarSeverity("success");
        setSnackbarOpen(true);
      } else {
        setErrorText("Error deleting skill weightage. Please try again.");
        setSnackbarMessage("Error deleting Skill Weightage. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
        console.error("Error deleting skill weightage:", response.statusText);
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
    const updatedWeightage = updatedData[index];
    const { skillWeightageId, clientId, ...rest } = updatedWeightage;
  
    const updatedJsonData = {
      ...rest,
      skillWeightageName: editedName,
      clientId: clientId, // Make sure to include clientId in the updated data
    };
  
    try {
      const response = await axios.put(
        `${API_BASE_URL}${SKILL_WEIGHTAGES}/${skillWeightageId}`, // Update the API endpoint
        updatedJsonData,
        { headers: header }
      );
  
      if (response.status === 200) {
        // Update the local state with the edited name
        updatedData[index] = { ...updatedWeightage, skillWeightageName: editedName };
        setTableData(updatedData);
        setEditingRow(-1); // Exit edit mode
        setEditIndex(-1);
        setErrorText(""); // Reset error text on successful update
      } else {
        setErrorText("Error updating skill weightage. Please try again.");
        console.error("Error updating skill weightage:", response.statusText);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };
  const handleEditClick = (index, name) => {
    setEditIndex(index);
    setEditedName(name); // Set the edited name to the current weightage name
    setEditingRow(index); // Enter edit mode for this row
  
    // Ensure that tableData[index] is defined before setting editedItem
    if (tableData[index]) {
      setEditedItem(tableData[index]); // Set the edited item
      setShowModal(true); // Show the modal
    } else {
      console.error("Invalid index or data for editing.");
    }
  };
  
  const handleCloseModal = () => {
    setShowModal(false);
    // Reset any necessary state variables if needed
    setEditingRow(-1);
    setEditIndex(-1);
  };
  const handleModalSave = async () => {
    try {
      // Construct the updated data object
      const updatedData = {
        skillWeightageId: editedItem.skillWeightageId,
        skillWeightageName: editedName,
        // Include any other fields you need to update
      };
  
      // Send a PUT request to update the skill weightage
      const response = await axios.put(
        `${API_BASE_URL}${SKILL_WEIGHTAGES}/${editedItem.skillWeightageId}`, // Update the API endpoint
        updatedData,
        { headers: header }
      );
  
      if (response.status === 200) {
        // Update the local state with the edited name
        const updatedTableData = tableData.map((item) =>
          item.skillWeightageId === editedItem.skillWeightageId
            ? { ...item, skillWeightageName: editedName }
            : item
        );
  
        setTableData(updatedTableData);
        setEditingRow(-1); // Exit edit mode
        setEditIndex(-1);
        setIsModalVisible(false);
        setErrorText(""); // Reset error text on successful update
        setPopupSnackbarMessage("Skill Weightage successfully updated");
        setPopupSnackbarSeverity("success");
        setPopupSnackbarOpen(true);
      } else {
        setErrorText("Error updating skill weightage. Please try again.");
        console.error("Error updating skill weightage:", response.statusText);
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
      <h4 className="text-center pt-3">Skill Weightage Creation</h4>
      
      <form onSubmit={handleSubmit} className="">
        <div className="row d-flex justify-content-center">
          <div className="col-12 col-md-4 col-lg-4">
            <input
              type="text"
              id="weightageName"
              name="weightageName"
              required
              value={weightageName}
              onChange={handleInputChange}
              className="form-control"
              placeholder="Skill Weightage Name"
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
          {weightageNames.length > 0 && (
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
                          {editingRow === rowIndex && column === "skillWeightageName" ? (
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
                                  onClick={() => handleUpdates(rowIndex, row.skillWeightageName)}
                                />
                              </div>
                              <div className="EditDeleteBtnStyling m-auto mx-1">
                                <DeleteTwoToneIcon
                                  className="edit- text-center myEditIcons"
                                  onClick={() => handleDelete(rowIndex, row.skillWeightageId)}
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
            Edit Skill Weightage
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
        <label htmlFor="editedName">Skill Weightage Name:</label>
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

export default SkillWeightage;
