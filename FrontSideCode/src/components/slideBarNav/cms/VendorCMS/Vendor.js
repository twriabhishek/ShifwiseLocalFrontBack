import React, { useState, useEffect } from 'react';
import axios from 'axios';
import LineNumberedTextarea from './LineNumberedTextarea';
import EditTwoToneIcon from '@mui/icons-material/EditTwoTone';
import DeleteTwoToneIcon from '@mui/icons-material/DeleteTwoTone';
import { IoCloseSharp, IoEyeSharp } from "react-icons/io5";
import { Modal } from 'react-bootstrap';
import { API_BASE_URL, SYSTEM, VENDORS } from '../../../constant-API/constants';

function Vendor() {
  const [vendorsData, setVendorsData] = useState([]);
  const [errorText, setErrorText] = useState('');
  const [vendorName, setVendorName] = useState('');
  const [template, setTemplate] = useState('');
  const [selectedSystem, setSelectedSystem] = useState('');
  const [systemData, setSystemData] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [selectedVendor, setSelectedVendor] = useState(null);

  useEffect(() => {
    fetchDataForTable();
    fetchSystemData();
  }, []);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const fetchDataForTable = async () => {
    try {
      const vendorResponse = await axios.get(
        `${API_BASE_URL}${VENDORS}`, { headers: header }
      );

      if (vendorResponse.status === 200) {
        const vendorData = vendorResponse.data;
        const vendorsWithEditMode = vendorData.map((vendor) => ({
          ...vendor,
          isEditMode: false,
        }));
        setVendorsData(vendorsWithEditMode);
      } else {
        setErrorText('Error fetching vendor data from the API.');
      }
    } catch (error) {
      setErrorText('Network error. Please try again.');
    }
  };

  const fetchSystemData = async () => {
    try {
      const systemResponse = await axios.get(
        `${API_BASE_URL}${SYSTEM}`, { headers: header }
      );

      if (systemResponse.status === 200) {
        const systemData = systemResponse.data;
        setSystemData(systemData);
      } else {
        setErrorText('Error fetching system data from the API.');
      }
    } catch (error) {
      setErrorText('Network error. Please try again.');
    }
  };

  const handleVendorNameChange = (event) => {
    setVendorName(event.target.value);
  };

  const formatJson = () => {
    try {
      const formattedJson = JSON.stringify(JSON.parse(template), null, 2);
      setTemplate(formattedJson);
    } catch (error) {
    }
  };

  const handleSubmit = () => {
    if (editMode && selectedVendor) {
      const { isEditMode, ...updatedVendor } = selectedVendor;
      updatedVendor.vendorName = vendorName;
      updatedVendor.template = template;

      // Remove the isEditMode property from the updatedVendor object
      delete updatedVendor.isEditMode;

      axios
        .put(
          `${API_BASE_URL}${VENDORS}/${updatedVendor.vendorId}`,
          updatedVendor,
          { headers: header }
        )
        .then((response) => {
          console.log('Vendor Updated Successfully:', response.data);
          fetchDataForTable();
          setIsModalOpen(false);
          setEditMode(false);
          setSelectedVendor(null);
        })
        .catch((error) => {
          console.error('Error updating vendor:', error);
        });
    } else {
      const selectedSystemData = systemData.find((system) => system.systemName === selectedSystem);

      if (selectedSystemData) {
        const jsonResponse = {
          systemId: selectedSystemData.systemId,
          vendorName: vendorName,
          template: template,
        };

        axios
          .post(
            `${API_BASE_URL}${VENDORS}`,
            jsonResponse,
            { headers: header } // Ensure the headers are passed correctly
          )
          .then((response) => {
            fetchDataForTable();
            setIsModalOpen(false);
          })
          .catch((error) => {
            console.error('Error sending JSON response:', error);
          });

      }
    }
  };
  const handleDeleteVendor = (vendor) => {
    axios
      .delete(
        `${API_BASE_URL}${VENDORS}/${vendor.vendorId}`,
        { headers: header }
      )
      .then((response) => {
        fetchDataForTable();
      })
      .catch((error) => {
        console.error('Error deleting vendor:', error);
      });
  };


  const handleCreateVendorClick = () => {
    setIsModalOpen(true);
    setVendorName('');
    setTemplate('');
    setSelectedSystem('');
    setEditMode(false);
    setSelectedVendor(null);
  };

  const handleEditVendor = (vendor) => {
    setIsModalOpen(true);
    setVendorName(vendor.vendorName);
    setTemplate(vendor.template);
    setEditMode(true);
    setSelectedVendor(vendor);
  };



  return (
    <div className="container" id='tableStartingStyling'>
      <h4 className="text-center pt-3">Vendor Creation</h4>
      <button className="buttonStyling p-2" onClick={handleCreateVendorClick}>
        Create Vendor
      </button>

      {isModalOpen && (
        <div id="ModelSection">
          <div className="modal">
            <div className="modal-content">
              <div className="d-flex justify-content-between align-items-center">
                <div></div>
                <h4>{editMode ? "Update" : "Create Vendor"}</h4>
                <span
                  className="modelCancelButton"
                  onClick={() => setIsModalOpen(false)}
                >
                  <IoCloseSharp />
                </span>
              </div>

              <div className="form-group">
                <div className="form-row">
                  {/* <label htmlFor="vendorName">Vendor Name:</label> */}
                  <input
                    type="text"
                    id="vendorName"
                    className="mb-2"
                    value={vendorName}
                    placeholder="Vendor Name "
                    onChange={handleVendorNameChange}
                  />
                </div>
              </div>
              {!editMode && (
                <div className="form-group">
                  <div className="form-row">
                    {/* <label htmlFor="systemDropdown">Select a System:</label> */}
                    <select
                      id="systemDropdown"
                      onChange={(e) => setSelectedSystem(e.target.value)}
                      value={selectedSystem}
                      placeholder="Select System"
                      className="mb-2"
                    >
                      <option value="">Select a System </option>
                      {systemData.map((system, index) => (
                        <option key={index} value={system.systemName}>
                          {system.systemName}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              )}
              <div className="form-group">
                <label htmlFor="template">Template:</label>
                <LineNumberedTextarea value={template} onChange={setTemplate} />
              </div>
              <div className="form-group">
                <button id="UserModuleBtn" onClick={handleSubmit}>
                  {editMode ? "Update" : "Submit"}
                </button>
                <span style={{ margin: "0 10px" }}></span>
                {/* <button id='UserModuleBtn' onClick={() => setIsModalOpen(false)}>Close</button> */}
              </div>
            </div>
          </div>
        </div>
      )}

      {vendorsData.length > 0 ? (
        <div className="table_overflow" id="VendorSection">
          <table className="">
            <thead>
              <tr className="">
                <th>Serial Number</th>
                <th>Vendor ID</th>
                <th>Vendor Name</th>
                <th>Template</th>
                <th>Action </th>
              </tr>
            </thead>
            <tbody className="">
              {vendorsData.map((vendor, index) => (
                <tr key={vendor.vendorId}>
                  <td>{index + 1}</td>
                  <td>{vendor.vendorId}</td>
                  <td>{vendor.vendorName}</td>
                  {/* <td>{vendor.template}</td> */}
                  <td><span className='fs-4'> <IoEyeSharp /> </span></td>
                  <td>
                    <div className="d-flex justify-content-center align-items-center">
                      <div className="EditDeleteBtnStyling m-auto mx-1">
                        <EditTwoToneIcon
                          className="edit-icon namespaceIcons myEditIcons"
                          onClick={() => handleEditVendor(vendor)}
                        />
                      </div>
                      <div className="EditDeleteBtnStyling m-auto mx-1">
                        <DeleteTwoToneIcon
                          className="edit-icon namespaceIcons myEditIcons"
                          onClick={() => handleDeleteVendor(vendor)}
                        />
                      </div>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        // <h5 className='text-center mt-4'>Vendor Data Loading................... </h5>
        <>
          <div className='d-flex justify-content-center'>
            <img src="../img/loading.gif" style={{ width: "50px", marginTop: "10px" }} alt="" />
          </div>
        </>
      )}



      {/* Model Created */}

    </div>

  );
}

export default Vendor;