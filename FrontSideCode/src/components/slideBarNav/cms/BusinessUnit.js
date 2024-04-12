import React, { useState, useEffect } from "react";
import axios from "axios";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import Draggable from "react-draggable";
import { Modal } from "antd";
import { Snackbar } from "@mui/material";
import Alert from "@mui/material/Alert";
import { API_BASE_URL, BUSINESS_UNIT, BUSINESS_UNIT_TABLE_LIST } from "../../constant-API/constants";
function BusinessUnit() {
  const [businessUnitName, setBusinessUnitName] = useState("");
  const [businessUnitNames, setBusinessUnitNames] = useState([]);
  const [inputWarning, setInputWarning] = useState("");
  const [editIndex, setEditIndex] = useState(-1);
  const [editedName, setEditedName] = useState("");
  const [errorText, setErrorText] = useState("");
  const [tableData, setTableData] = useState([]);
  const [tableColumns, setTableColumns] = useState([]);
  const [editingRow, setEditingRow] = useState(-1);

  const [editedItem, setEditedItem] = useState({});
  const [openModal, setOpenModal] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [dragging, setDragging] = useState(false);
  const [disabled, setDisabled] = useState(true);
  const [inputFocus, setInputFocus] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
  const [popupSnackbarOpen, setPopupSnackbarOpen] = useState(false);
  const [popupSnackbarMessage, setPopupSnackbarMessage] = useState("");
  const [popupSnackbarSeverity, setPopupSnackbarSeverity] = useState("success");

  useEffect(() => {
    fetchDataForTable();
    fetchExistingBusinessUnitNames();
  }, []);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchDataForTable = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}${BUSINESS_UNIT}`, { headers: header }
      );

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const columns = Object.keys(data[0]).filter(
            (column) => column !== "businessUnitId" && column !== "clientId"
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
      console.error("Network error:", error);
    }
  };

  const fetchExistingBusinessUnitNames = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/business-units`, { headers: header });

      if (response.status === 200) {
        const data = response.data;
        if (data.length > 0) {
          const names = data.map((item) => item.businessUnitName);
          setBusinessUnitNames(names);
        }
      } else {
        setErrorText("Error fetching existing business unit names.");
      }
    } catch (error) {
      setErrorText("Network error. Please try again.");
      console.error("Network error:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (businessUnitName.trim() !== "") {
      const jsonData = { businessUnitName: businessUnitName };
      try {
        const response = await axios.post(
          `${API_BASE_URL}${BUSINESS_UNIT}`, // Update the API endpoint
          jsonData,
          { headers: header }
        );

        if (response.status === 201) {
          setBusinessUnitNames([...businessUnitNames, businessUnitName]);
          setBusinessUnitName("");
          setErrorText("");
          setSnackbarMessage("Business Unit Name successfully added");
          setSnackbarSeverity("success");
          setSnackbarOpen(true);
        } else {
          setErrorText("Error adding business unit name. Please try again.");
          setSnackbarMessage(
            "Error adding business unit name. Please try again."
          );
          setSnackbarSeverity("error");
          setSnackbarOpen(true);
          console.error(
            "Error adding business unit name:",
            response.statusText
          );
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

    // Add your validation logic for uppercase letters if needed
    const uppercaseValue = inputValue.toUpperCase();

    setBusinessUnitName(uppercaseValue);

    if (inputValue !== uppercaseValue) {
      setInputWarning("Only uppercase letters are allowed.");
    } else {
      setInputWarning("");
    }
  };

  // Handle Delete Button 
  const handleDelete = async (index, businessUnitId) => {
    try {
      const response = await axios.delete(`${API_BASE_URL}${BUSINESS_UNIT}/${businessUnitId}`, { headers: header });

      if (response.status === 200) {
        // Business unit deleted successfully from the API
        const updatedData = [...tableData];
        updatedData.splice(index, 1);
        setTableData(updatedData);

        const updatedNames = [...businessUnitNames];
        updatedNames.splice(index, 1);
        setBusinessUnitNames(updatedNames);

        setErrorText("");
        // Show delete success alert
        setSnackbarMessage("Business Unit successfully deleted");
        setSnackbarSeverity("success");
        setSnackbarOpen(true);
      } else {
        setErrorText("Error deleting business unit. Please try again.");
        // Show delete error alert
        setSnackbarMessage("Error deleting business unit. Please try again.");
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
    const updatedBusinessUnit = updatedData[index];
    const { businessUnitId, clientId, ...rest } = updatedBusinessUnit;

    const updatedJsonData = {
      ...rest,
      businessUnitName: editedName,
      clientId: clientId, // Make sure to include clientId in the updated data
    };

    try {
      const response = await axios.put(
        `${API_BASE_URL}${BUSINESS_UNIT}/${businessUnitId}`, // Update the API endpoint
        updatedJsonData,
        { headers: header }
      );

      if (response.status === 200) {
        // Update the local state with the edited name
        updatedData[index] = {
          ...updatedBusinessUnit,
          businessUnitName: editedName,
        };
        setTableData(updatedData);
        setEditingRow(-1); // Exit edit mode
        setEditIndex(-1);
        setErrorText(""); // Reset error text on successful update
      } else {
        setErrorText("Error updating business unit. Please try again.");
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

  const handleModalSave = async () => {
    try {
      const updatedData = {
        businessUnitId: editedItem.businessUnitId,
        businessUnitName: editedName,
      };

      const response = await axios.put(
        `${API_BASE_URL}${BUSINESS_UNIT}/${editedItem.businessUnitId}`,
        updatedData,
        { headers: header }
      );

      if (response.status === 200) {
        const updatedTableData = tableData.map((item) =>
          item.businessUnitId === editedItem.businessUnitId
            ? { ...item, businessUnitName: editedName }
            : item
        );

        setTableData(updatedTableData);
        setEditingRow(-1);
        setEditIndex(-1);
        // This assignment is used to track the index of the currently edited item.
        // It is intentionally set to -1 when exiting edit mode.
        setIsModalVisible(false);
        setErrorText("");
        // Set popup alert for success
        setPopupSnackbarMessage("Business Unit successfully updated");
        setPopupSnackbarSeverity("success");
        setPopupSnackbarOpen(true);
      } else {
        setErrorText("Error updating business unit. Please try again.");
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
      <h4 className="text-center pt-3">Business Unit Creation</h4>

      <form onSubmit={handleSubmit} className="">
        <div className="row d-flex justify-content-center">
          <div className="col-12 col-md-4 col-lg-4">
            <input
              type="text"
              id="businessUnitName"
              name="businessUnitName"
              required
              value={businessUnitName}
              onChange={handleInputChange}
              className="form-control"
              placeholder="Business Unit Name"
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
          {businessUnitNames.length > 0 && (
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
                          {editingRow === rowIndex &&
                            column === "businessUnitName" ? (
                            <input
                              type="text"
                              value={editedName}
                              onChange={(e) => setEditedName(e.target.value)}
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
                                  onClick={() =>
                                    handleUpdates(
                                      rowIndex,
                                      row.businessUnitName
                                    )
                                  }
                                />
                              </div>
                              <div className="EditDeleteBtnStyling m-auto mx-1">
                                <DeleteTwoToneIcon
                                  className="edit- text-center myEditIcons"
                                  onClick={() =>
                                    handleDelete(rowIndex, row.businessUnitId)
                                  }
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
            Edit Business Unit
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
        <label htmlFor="editedName">Business Unit Name:</label>
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
export default BusinessUnit;
