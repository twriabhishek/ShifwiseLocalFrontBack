import React, { useState, useEffect } from "react";
import axios from "axios";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import { Modal } from "antd";
import Draggable from "react-draggable";
import { Snackbar } from "@mui/material";
import Alert from "@mui/material/Alert";
import { API_BASE_URL, PROCESS_UNIT } from "../../constant-API/constants";

function ProcessUnit() {
  const [processUnitName, setProcessUnitName] = useState("");
  const [processUnitNames, setProcessUnitNames] = useState([]);
  const [inputWarning, setInputWarning] = useState("");
  const [editIndex, setEditIndex] = useState(-1);
  const [editedName, setEditedName] = useState("");
  const [errorText, setErrorText] = useState("");
  const [tableData, setTableData] = useState([]);
  const [tableColumns, setTableColumns] = useState([]);
  const [editingRow, setEditingRow] = useState(-1);
  const [editedItem, setEditedItem] = useState({});
  const [showModal, setShowModal] = useState(false);
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
    fetchExistingProcessUnitNames();
  }, []);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchDataForTable = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}${PROCESS_UNIT}`, // Update the API endpoint
        { headers: header }
      );

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const columns = Object.keys(data[0]).filter(
            (column) => column !== "processUnitId" && column !== "clientId"
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

  const fetchExistingProcessUnitNames = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}${PROCESS_UNIT}`, // Update the API endpoint
        { headers: header }
      );

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const names = data.map((item) => item.processUnitName);
          setProcessUnitNames(names);
        }
      } else {
        setErrorText("Error fetching existing process unit names.");
        console.error(
          "Error fetching existing process unit names:",
          response.statusText
        );
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (processUnitName.trim() !== "") {
      const jsonData = { processUnitName: processUnitName };

      try {
        const response = await axios.post(
          `${API_BASE_URL}${PROCESS_UNIT}`, // Update the API endpoint
          jsonData,
          { headers: header }
        );

        if (response.status === 201) {
          setProcessUnitNames([...processUnitNames, processUnitName]);
          setProcessUnitName("");
          setErrorText("");
          setSnackbarMessage("Process Unit Name successfully added");
          setSnackbarSeverity("success");
          setSnackbarOpen(true);
        } else {
          setErrorText("Error adding process unit name. Please try again.");
          setSnackbarMessage(
            "Error adding process unit name. Please try again."
          );
          setSnackbarSeverity("error");
          setSnackbarOpen(true);
          console.error("Error adding process unit name:", response.statusText);
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

  const handleInputChange = (e) => {
    const inputValue = e.target.value;
  
    // Add your validation logic for uppercase letters if needed
    const uppercaseValue = inputValue.toUpperCase();
  
    setProcessUnitName(uppercaseValue);
  
    if (inputValue !== uppercaseValue) {
      setInputWarning("Only uppercase letters are allowed.");
    } else {
      setInputWarning("");
    }
  };
  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
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
  
  const handleDelete = async (index, processUnitId) => {
    try {
      const response = await axios.delete(
        `${API_BASE_URL}${PROCESS_UNIT}/${processUnitId}`, // Update the API endpoint
        { headers: header }
      );
  
      if (response.status === 200) {
        // Process unit deleted successfully from the API
        const updatedData = [...tableData];
        updatedData.splice(index, 1);
        setTableData(updatedData);
  
        const updatedNames = [...processUnitNames];
        updatedNames.splice(index, 1);
        setProcessUnitNames(updatedNames);
  
        setErrorText("");
          // Show delete success alert
          setSnackbarMessage("Process Unit successfully deleted");
          setSnackbarSeverity("success");
          setSnackbarOpen(true);
      } else {
        setErrorText("Error deleting process unit. Please try again.");
        console.error("Error deleting process unit:", response.statusText);
         // Show delete error alert
         setSnackbarMessage("Error deleting process unit. Please try again.");
         setSnackbarSeverity("error");
         setSnackbarOpen(true);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
      // Show network error alert
      setSnackbarMessage("Network error. Please try again.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
    }
  };
  
  const handleSave = async (index) => {
    const updatedData = [...tableData];
    const updatedProcessUnit = updatedData[index];
    const { processUnitId, clientId, ...rest } = updatedProcessUnit;
  
    const updatedJsonData = {
      ...rest,
      processUnitName: editedName,
      clientId: clientId, // Make sure to include clientId in the updated data
    };
  
    try {
      const response = await axios.put(
        `${API_BASE_URL}${PROCESS_UNIT}/${processUnitId}`, // Update the API endpoint
        updatedJsonData,
        { headers: header }
      );
  
      if (response.status === 200) {
        // Update the local state with the edited name
        updatedData[index] = { ...updatedProcessUnit, processUnitName: editedName };
        setTableData(updatedData);
        setEditingRow(-1); // Exit edit mode
        setEditIndex(-1);
        setErrorText(""); // Reset error text on successful update
      } else {
        setErrorText("Error updating process unit. Please try again.");
        console.error("Error updating process unit:", response.statusText);
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

  const handleModalSave = async () => {
    try {
      const updatedData = {
        processUnitId: editedItem.processUnitId,
        processUnitName: editedName,
      };

      const response = await axios.put(
        `${API_BASE_URL}${PROCESS_UNIT}/${editedItem.processUnitId}`,
        updatedData,
        { headers: header }
      );

      if (response.status === 200) {
        const updatedTableData = tableData.map((item) =>
          item.processUnitId === editedItem.processUnitId
            ? { ...item, processUnitName: editedName }
            : item
        );

        setTableData(updatedTableData);
        setEditingRow(-1);
        setEditIndex(-1);
        setIsModalVisible(false);
        setErrorText("");
          // Set popup alert for success
          setPopupSnackbarMessage("Process Unit successfully updated");
          setPopupSnackbarSeverity("success");
          setPopupSnackbarOpen(true);
      } else {
        setErrorText("Error updating process unit. Please try again.");
        console.error("Error updating process unit:", response.statusText);
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
      <h4 className="text-center pt-3">Process Unit Creation</h4>
      
      <form onSubmit={handleSubmit} className="">
        <div className="row d-flex justify-content-center">
          <div className="col-12 col-md-4 col-lg-4">
            <input
              type="text"
              id="processUnitName"
              name="processUnitName"
              required
              value={processUnitName}
              onChange={handleInputChange}
              className="form-control"
              placeholder="Process Unit Name"
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
          {processUnitNames.length > 0 && (
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
                          {editingRow === rowIndex && column === "processUnitName" ? (
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
                                  onClick={() => handleUpdates(rowIndex, row.processUnitName)}
                                />
                              </div>
                              <div className="EditDeleteBtnStyling m-auto mx-1">
                                <DeleteTwoToneIcon
                                  className="edit- text-center myEditIcons"
                                  onClick={() => handleDelete(rowIndex, row.processUnitId)}
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
        {/* Snackbar for displaying creating and success/error messages */}
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
            Edit Process Unit
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
        <label htmlFor="editedName">Process Unit Name:</label>
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

export default ProcessUnit;
