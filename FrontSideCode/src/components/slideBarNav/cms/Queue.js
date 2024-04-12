import React, { useState, useEffect } from "react";
import axios from "axios";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import { Modal } from "antd";
import Draggable from "react-draggable";
import { Snackbar } from "@mui/material";
import Alert from "@mui/material/Alert";
import { API_BASE_URL, QUEUES } from "../../constant-API/constants";



function Queue() {
  const [queueName, setQueueName] = useState("");
  const [queueNames, setQueueNames] = useState([]);
  const [inputWarning, setInputWarning] = useState("");
  const [editIndex, setEditIndex] = useState(-1);
  const [editedName, setEditedName] = useState("");
  const [errorText, setErrorText] = useState("");
  const [tableData, setTableData] = useState([]);
  const [tableColumns, setTableColumns] = useState([]);
  const [editingRow, setEditingRow] = useState(-1);
  const [editedItem, setEditedItem] = useState({});
  const [showModal, setShowModal] = useState(false);
  const [openModal, setOpenModal] = useState(false);
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
    fetchExistingQueueNames();
  }, []);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchDataForTable = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}${QUEUES}`, { headers: header });

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const columns = Object.keys(data[0]).filter(
            (column) => column !== "queueId" && column !== "clientId"
          );
          setTableColumns(columns);
        }
        setTableData(data);
        setErrorText("");
      } else {
        setErrorText("Error fetching data from the API.");
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
    }
  };

  const fetchExistingQueueNames = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}${QUEUES}`, { headers: header });
      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const names = data.map((item) => item.queueName);
          setQueueNames(names);
        }
      } else {
        setErrorText("Error fetching existing queue names.");
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
    }
  };
  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (queueName.trim() !== "") {
      const jsonData = { queueName: queueName };

      try {
        const response = await axios.post(`${API_BASE_URL}${QUEUES}`, jsonData, { headers: header });

        if (response.status === 201) {
          setQueueNames([...queueNames, queueName]);
          setQueueName("");
          setErrorText("");
          setSnackbarMessage("Queue Name successfully added");
          setSnackbarSeverity("success");
          setSnackbarOpen(true);
        } else {
          setErrorText("Error adding queue name. Please try again.");
          setSnackbarMessage(
            "Error adding queue unit name. Please try again."
          );
          setSnackbarSeverity("error");
          setSnackbarOpen(true);
        }
      } catch (error) {
        setErrorText("Network error. Please try again.");
        setSnackbarMessage("Network error. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
      }
    }
    fetchDataForTable();
  };

  const handleInputChange = (e) => {
    const inputValue = e.target.value;

    // Add your validation logic if needed
    setQueueName(inputValue);

    // Add your validation warning logic if needed
    if (inputValue.trim() !== "") {
      setInputWarning("");
    } else {
      setInputWarning("Queue name cannot be empty.");
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

  const handleDelete = async (index, queueId) => {
    try {
      const response = await axios.delete(
        `${API_BASE_URL}${QUEUES}/${queueId}`, // Update the API endpoint
        { headers: header }
      );

      if (response.status === 200) {
        // Queue deleted successfully from the API
        const updatedData = [...tableData];
        updatedData.splice(index, 1);
        setTableData(updatedData);

        const updatedNames = [...queueNames];
        updatedNames.splice(index, 1);
        setQueueNames(updatedNames);

        setErrorText("");
        setSnackbarMessage("Queue successfully deleted");
        setSnackbarSeverity("success");
        setSnackbarOpen(true);
      } else {
        setErrorText("Error deleting queue. Please try again.");
        // Show delete error alert
        setSnackbarMessage("Error deleting Group. Please try again.");
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      setSnackbarMessage("Network error. Please try again.");
      setSnackbarSeverity("error");
      setSnackbarOpen(true);
    }
  };

  const handleSave = async (index) => {
    const updatedData = [...tableData];
    const updatedQueue = updatedData[index];
    const { queueId, clientId, ...rest } = updatedQueue;

    const updatedJsonData = {
      ...rest,
      queueName: editedName,
      clientId: clientId, // Make sure to include clientId in the updated data
    };

    try {
      const response = await axios.put(
        `${API_BASE_URL}${QUEUES}/${queueId}`, // Update the API endpoint
        updatedJsonData,
        { headers: header }
      );

      if (response.status === 200) {
        // Update the local state with the edited name
        updatedData[index] = { ...updatedQueue, queueName: editedName };
        setTableData(updatedData);
        setEditingRow(-1); // Exit edit mode
        setEditIndex(-1);
        setErrorText(""); // Reset error text on successful update
      } else {
        setErrorText("Error updating queue. Please try again.");
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
    }
  };
  const handleCloseModal = () => {
    setShowModal(false);
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

  const handleModalSave = async () => {
    try {
      const updatedData = {
        queueId: editedItem.queueId,
        queueName: editedName,
      };

      const response = await axios.put(
        `${API_BASE_URL}${QUEUES}/${editedItem.queueId}`,
        updatedData,
        { headers: header }
      );

      if (response.status === 200) {
        const updatedTableData = tableData.map((item) =>
          item.queueId === editedItem.queueId
            ? { ...item, queueName: editedName }
            : item
        );

        setTableData(updatedTableData);
        setEditingRow(-1);
        setEditIndex(-1);
        setIsModalVisible(false);
        setErrorText("");
        setPopupSnackbarMessage("Queue successfully updated");
        setPopupSnackbarSeverity("success");
        setPopupSnackbarOpen(true);
      } else {
        setErrorText("Error updating queue. Please try again.");
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
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
      <h4 className="text-center pt-3">Queue Creation</h4>

      <form onSubmit={handleSubmit} className="">
        <div className="row d-flex justify-content-center">
          <div className="col-12 col-md-4 col-lg-4">
            <input
              type="text"
              id="queueName"
              name="queueName"
              required
              value={queueName}
              onChange={handleInputChange}
              className="form-control"
              placeholder="Queue Name"
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
          {queueNames.length > 0 && (
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
                          {editingRow === rowIndex && column === "queueName" ? (
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
                                  onClick={() => handleUpdates(rowIndex, row.queueName)}
                                />
                              </div>
                              <div className="EditDeleteBtnStyling m-auto mx-1">
                                <DeleteTwoToneIcon
                                  className="edit- text-center myEditIcons"
                                  onClick={() => handleDelete(rowIndex, row.queueId)}
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
            Edit Queue
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
        <label htmlFor="editedName">Queue Name:</label>
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

export default Queue;
