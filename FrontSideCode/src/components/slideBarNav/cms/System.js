import React, { useState, useEffect } from "react";
import axios from "axios";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import { Modal } from "antd";
import Draggable from "react-draggable";
import { Snackbar } from "@mui/material";
import Alert from "@mui/material/Alert";
import { API_BASE_URL, SYSTEM } from "../../constant-API/constants";

function System() {
  const [systemName, setSystemName] = useState("");
  const [systemNames, setSystemNames] = useState([]);
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
    // Am Fetching data for the table from the API when the component mounts
    fetchDataForTable();
    // Am Fetching existing system names when the component mounts
    fetchExistingSystemNames();
  }, []);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchDataForTable = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}${SYSTEM}`, { headers: header });

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const columns = Object.keys(data[0]).filter(
            (column) => column !== "clientId"
          );
          setTableColumns(columns);
        }
        setTableData(data);
        setErrorText(""); // Reset the error text on successful response
      } else {
        setErrorText("Error fetching data from the API.");
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
    }
  };

  const fetchExistingSystemNames = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}${SYSTEM}`,
        { headers: header }
      );

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const names = data.map((item) => item.systemName);
          setSystemNames(names);
        }
      } else {
        setErrorText("Error fetching existing system names.");

      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (systemName.trim() !== "") {
      const jsonData = { systemName: systemName };

      try {
        const response = await axios.post(
          `${API_BASE_URL}${SYSTEM}`,
          jsonData,
          { headers: header }
        );

        if (response.status === 201) {
          setSystemNames([...systemNames, systemName]);
          setSystemName("");
          setErrorText("");
          setSnackbarMessage("System Name successfully added");
          setSnackbarSeverity("success");
          setSnackbarOpen(true);

          // Log the JSON data sent to the API
        } else {
          // Handle error response from the API
          setErrorText("Error adding systemName. Please try again.");
          setSnackbarMessage(
            "Error adding system name. Please try again."
          );
          setSnackbarSeverity("error");
          setSnackbarOpen(true);
        }
      } catch (error) {
        // Handle network error
        setErrorText("Network error. Please try again.");
        setSnackbarMessage("Network error. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
      }
    }
    fetchDataForTable();
  };


  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };
  const handleInputChange = (e) => {
    const inputValue = e.target.value;
    const uppercaseValue = inputValue.replace(/[^A-Z]/g, "");
    setSystemName(uppercaseValue);

    if (inputValue !== uppercaseValue) {
      setInputWarning("Only uppercase letters are allowed.");
    } else {
      setInputWarning("");
    }
  };


  const handleDelete = async (index, systemId) => {
    try {
      const response = await axios.delete(
        `${API_BASE_URL}${SYSTEM}/${systemId}`,
        { headers: header }
      );
      console.log(response);

      if (response.status === 200) {
        // System deleted successfully from the API
        const updatedData = [...tableData];
        updatedData.splice(index, 1);
        setTableData(updatedData);

        const updatedNames = [...systemNames];
        updatedNames.splice(index, 1);
        setSystemNames(updatedNames);

        setErrorText("");
        setSnackbarMessage("System successfully deleted");
        // setSnackbarMessage(response.data.message);

        setSnackbarSeverity("success");
        setSnackbarOpen(true);
      } else {
        setErrorText("Error deleting system. Please try again.");
        setSnackbarMessage("Error deleting System. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
      }
    } catch (error) {
      console.log(error);
      setErrorText("Network error. Please try again.");
      setSnackbarMessage("Network error. Please try again.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
    }
  };
  const handleSave = async (index) => {
    const updatedData = [...tableData];
    const updatedSystem = updatedData[index];
    const { systemId, clientId, ...rest } = updatedSystem;

    const updatedJsonData = {
      ...rest,
      systemName: editedName,
      clientId: clientId, // Make sure to include clientId in the updated data
    };

    try {
      const response = await axios.put(
        `${API_BASE_URL}${SYSTEM}/${systemId}`,
        updatedJsonData,
        { headers: header }
      );

      if (response.status === 200) {
        // Update the local state with the edited name
        updatedData[index] = { ...updatedSystem, systemName: editedName };
        setTableData(updatedData);
        setEditingRow(-1); // Exit edit mode
        setEditIndex(-1);
        setErrorText(""); // Reset error text on successful update
      } else {
        setErrorText("Error updating system. Please try again.");
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
    }
  };

  const handleEditClick = (index, name) => {
    setEditIndex(index);
    setEditedName(name);

    if (tableData[index]) {
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
      const updatedData = [...tableData];
      const updatedSystem = updatedData[editIndex];
      const { systemId, clientId, ...rest } = updatedSystem;

      const updatedJsonData = {
        ...rest,
        systemName: editedName,
        clientId: clientId, // Make sure to include clientId in the updated data
      };

      const response = await axios.put(`${API_BASE_URL}${SYSTEM}/${systemId}`, updatedJsonData, { headers: header });

      if (response.status === 200) {
        updatedData[editIndex] = { ...updatedSystem, systemName: editedName };
        setTableData(updatedData);
        setEditingRow(-1);
        setEditIndex(-1);
        setIsModalVisible(false);
        setErrorText("");
        setPopupSnackbarMessage("System successfully updated");
        setPopupSnackbarSeverity("success");
        setPopupSnackbarOpen(true);
      } else {
        setErrorText("Error updating system. Please try again.");
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
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
    <div className="">
      <h4 className="text-center pt-3">System Creation</h4>

      <form onSubmit={handleSubmit} className="">
        <div className="row d-flex justify-content-center">
          <div className="col-12 col-md-4 col-lg-4">
            {/* <label htmlFor="systemName" className="text-center mb-1 ">System Name </label>  */}
            <input
              type="text"
              id="systemName"
              name="systemName"
              required
              value={systemName}
              onChange={handleInputChange}
              className=" form-control"
              placeholder="System Name"
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
          {systemNames.length > 0 && (
            <div className="table_overflow" >

              {/* Render the dynamic table */}
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
                          {editingRow === rowIndex && column === "systemName" ? (
                            <input
                              type="text"
                              value={editedName}
                              onChange={(e) =>
                                setEditedName(e.target.value.toUpperCase())
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
                              <div className=" m-auto mx-1" >
                                <EditTwoToneIcon
                                  className="edit- text-center myEditIcons"
                                  onClick={() => handleUpdates(rowIndex, row.systemName)}


                                />
                              </div>
                              <div className=" m-auto mx-1">
                                <DeleteTwoToneIcon
                                  className="edit- text-center myEditIcons"
                                  onClick={() => handleDelete(rowIndex, row.systemId)}
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
            Edit System
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
        <label htmlFor="editedName">System Name:</label>
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
export default System;
