// SystemConfig.js
import React, { useState, useEffect } from "react";
import axios from "axios";
import LineNumberedTextarea from "./VendorCMS/LineNumberedTextarea";
import { IoCloseSharp } from "react-icons/io5";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import VisibilityTwoToneIcon from "@material-ui/icons/VisibilityTwoTone";
import Tooltip from "@mui/material/Tooltip";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { API_BASE_URL, CLIENTLIST, SYSTEM, SYSTEM_CONFIG, TENANT_NAMESPACES, VENDORS } from "../../constant-API/constants";

const SystemConfig = () => {
  const [systems, setSystems] = useState([]);
  const [vendors, setVendors] = useState([]);
  const [clients, setClients] = useState([]);
  const [selectedSystem, setSelectedSystem] = useState("");
  const [selectedVendor, setSelectedVendor] = useState("");
  const [selectedClient, setSelectedClient] = useState("");
  const [namespaces, setNamespaces] = useState([]);

  const [templates, setTemplates] = useState([]);
  const [selectedTemplate, setSelectedTemplate] = useState("");
  const [filteredTemplates, setFilteredTemplates] = useState([]);
  const [selectedSystemId, setSelectedSystemId] = useState("");
  const [toSelectedSystemId, setToSelectedSystemId] = useState("");
  const [vendorsForSystem, setVendorsForSystem] = useState([]);
  const [templatesForSystem, setTemplatesForSystem] = useState([]);
  const [toSelectedSystem, setToSelectedSystem] = useState("");
  const [toSelectedVendor, setToSelectedVendor] = useState("");
  const [toSelectedTemplate, setToSelectedTemplate] = useState("");
  const [toVendorsForSystem, setToVendorsForSystem] = useState([]);
  const [toTemplatesForSystem, setToTemplatesForSystem] = useState([]);
  const [isTemplateModalOpen, setIsTemplateModalOpen] = useState(false);
  const [isToTemplateModalOpen, setIsToTemplateModalOpen] = useState(false);
  const [selectedFromSystem, setSelectedFromSystem] = useState("");
  const [selectedFromVendor, setSelectedFromVendor] = useState("");
  const [selectedToSystem, setSelectedToSystem] = useState("");
  const [selectedToVendor, setSelectedToVendor] = useState("");
  const [systemConfigName, setSystemConfigName] = useState("");

  const [selectedVendorId, setSelectedVendorId] = useState("");
  const [toSelectedVendorId, setToSelectedVendorId] = useState("");
  const [clientId, setClientId] = useState("");
  const [selectedNamespaceId, setSelectedNamespaceId] = useState("");
  const [selectedNamespace, setSelectedNamespace] = useState("");
  const [updatedTemplate, setUpdatedTemplate] = useState("");
  const [isTemplateUpdated, setIsTemplateUpdated] = useState(false);
  const [isToTemplateUpdated, setIsToTemplateUpdated] = useState(false);
  const [systemConfigs, setSystemConfigs] = useState([]);
  const [viewTemplateModalOpen, setViewTemplateModalOpen] = useState(false);
  const [viewedFromVendorTemplate, setViewedFromVendorTemplate] = useState("");
  const [viewedToVendorTemplate, setViewedToVendorTemplate] = useState("");
  const [isCombinedTemplateModalOpen, setIsCombinedTemplateModalOpen] =
    useState(false);
  const [editingSystemConfigId, setEditingSystemConfigId] = useState(null);

  const header = {
    Authorization: localStorage.getItem("token"),
  };

  const userRole = localStorage.getItem("roles");

  // "From" System Dropdown Change
  const handleFromSystemChange = (event) => {
    const selectedSystemName = event.target.value;
    setSelectedSystem(selectedSystemName);

    // Find the system ID based on the selected system name
    const selectedSystem = systems.find(
      (system) => system.systemName === selectedSystemName
    );


    if (selectedSystem) {
      const systemId = selectedSystem.systemId;
      setSelectedSystemId(systemId);

      // Fetch vendors for the selected system
      axios
        .get(`${API_BASE_URL}${VENDORS}${SYSTEM}/${systemId}`, { headers: header }
        )
        .then((response) => {
          setVendorsForSystem(response.data);
        })
        .catch((error) => {
          console.error(
            "Error fetching vendors for the selected system:",
            error
          );
        });

      // Fetch templates for the selected system
      axios
        .get(`${API_BASE_URL}${VENDORS}${SYSTEM}/${systemId}`,
          // `${API_BASE_URL}/vendors/system/${systemId}`,
          { headers: header }
        )
        .then((response) => {
          setTemplatesForSystem(response.data);
        })
        .catch((error) => {
          console.error(
            "Error fetching templates for the selected system:",
            error
          );
        });
    }
  };

  // "To" System Dropdown Change
  const handleToSystemChange = (event) => {
    const selectedSystemName = event.target.value;

    setToSelectedSystem(selectedSystemName);

    // Find the system ID based on the selected system name
    const selectedSystem = systems.find(
      (system) => system.systemName === selectedSystemName
    );


    if (selectedSystem) {
      const systemId = selectedSystem.systemId;
      setToSelectedSystemId(systemId);

      // Fetch vendors for the selected system
      axios.get(`${API_BASE_URL}/vendors/system/${systemId}`, { headers: header })
        .then((response) => {
          // Set state for "to" vendors
          setToVendorsForSystem(response.data);
        })
        .catch((error) => {
          console.error(
            "Error fetching vendors for the selected system:",
            error
          );
        });

      // Fetch templates for the selected system
      axios
        .get(
          `${API_BASE_URL}/vendors/system/${systemId}`,
          { headers: header }
        )
        .then((response) => {
          // Set state for "to" templates
          setToTemplatesForSystem(response.data);
        })
        .catch((error) => {
          console.error(
            "Error fetching templates for the selected system:",
            error
          );
        });
    }
  };

  // Modify handleVendorChange function
  const handleVendorChange = (event) => {
    const selectedVendorName = event.target.value;
    setSelectedVendor(selectedVendorName);

    // Find the vendor ID based on the selected vendor name
    const selectedVendor = vendorsForSystem.find(
      (vendor) => vendor.vendorName === selectedVendorName
    );

    if (selectedVendor) {
      const vendorId = selectedVendor.vendorId;
      setSelectedVendorId(vendorId); // Set the selected vendorId

      // Find the template for the selected vendor
      const templateForVendor = templatesForSystem.find(
        (template) => template.vendorName === selectedVendorName
      );

      // Set the selected template
      setSelectedTemplate(templateForVendor ? templateForVendor.template : "");

      // Open the template modal directly
      setIsTemplateModalOpen(true);
    }
  };
  // Modify handleToVendorChange function
  const handleToVendorChange = (event) => {
    const selectedVendorName = event.target.value;
    setToSelectedVendor(selectedVendorName);

    // Find the vendor ID based on the selected vendor name
    const selectedVendor = toVendorsForSystem.find(
      (vendor) => vendor.vendorName === selectedVendorName
    );

    if (selectedVendor) {
      const vendorId = selectedVendor.vendorId;
      setToSelectedVendorId(vendorId); // Set the selected vendorId

      // Find the template for the selected vendor
      const templateForVendor = toTemplatesForSystem.find(
        (template) => template.vendorName === selectedVendorName
      );

      // Set the selected template
      setToSelectedTemplate(
        templateForVendor ? templateForVendor.template : ""
      );

      // Open the to template modal directly
      setIsToTemplateModalOpen(true);
    }
  };

  useEffect(() => {
    // Fetch data for the system dropdown
    axios
      .get(`${API_BASE_URL}${SYSTEM}`,
        { headers: header }
      )
      .then((response) => {
        setSystems(response.data);
      })
      .catch((error) => {
        console.error("Error fetching systems:", error);
      });

    //fetch namespace
    axios
      .get(`${API_BASE_URL}${TENANT_NAMESPACES}`,
        { headers: header }
      )
      .then((response) => {
        setNamespaces(response.data);
      })
      .catch((error) => {
        console.error("Error fetching namespaces:", error);
      });
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(`${API_BASE_URL}${CLIENTLIST}`,
          { headers: header }
        );
        if (Array.isArray(response.data)) {
          if (response.data.length > 0 && response.data[0].clientId) {
            setClients(response.data);
          } else {
            console.error(
              "Response data does not have the expected structure:",
              response.data
            );
          }
        } else {
          console.error("Response data is not an array:", response.data);
        }
      } catch (error) {
        console.error("Error fetching client data:", error);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    axios
      .get(`${API_BASE_URL}${SYSTEM_CONFIG}`,
        { headers: header }
      )
      .then((response) => {
        setSystemConfigs(response.data);
      })
      .catch((error) => {
        console.error("Error fetching system configurations:", error);
      });
  }, []);

  const handleClientChange = (e) => {
    const selectedClientId = e.target.value;
    setClientId(selectedClientId);
    setSelectedClient(e.target.options[e.target.selectedIndex].text);
  };
  const handleNamespaceChange = (e) => {
    const selectedNamespaceId = e.target.value;
    setSelectedNamespaceId(selectedNamespaceId);
    setSelectedNamespace(e.target.options[e.target.selectedIndex].text);
  };

  const handleSubmit = () => {
    console.log('SUbmit');
    const generatedSystemConfigName = `From: ${selectedSystem} --> ${selectedVendor} To: ${toSelectedSystem} --> ${toSelectedVendor}`;

    // Update state with the generated systemConfigName
    setSystemConfigName(generatedSystemConfigName);
    const parsedClientId = parseInt(clientId);
    const postData = {
      clientId: !isNaN(parsedClientId) ? parsedClientId : 0,
      namespaceId: selectedNamespaceId,
      fromSystemId: selectedSystemId,
      fromVendorId: selectedVendorId,
      fromVendorTemplate: selectedTemplate,
      toSystemId: toSelectedSystemId,
      toVendorId: toSelectedVendorId,
      toVendorTemplate: toSelectedTemplate,
      serviceName: generatedSystemConfigName,
    };


    // Send the POST request
    axios.post(`${API_BASE_URL}${SYSTEM_CONFIG}`, postData, { headers: header }).then((response) => {
      toast.success("Successfully Saved", {
        position: toast.POSITION.TOP_RIGHT,
      });
      setClientId("");
      setSelectedSystem("");
      setSelectedVendor("");
      setToSelectedSystemId("");
      setToSelectedSystem("");
      setToSelectedVendor("");

      setSelectedNamespaceId("");
      setSelectedTemplate("");
      setToSelectedVendorId("");
      setToSelectedTemplate("");
    })
      .catch((error) => {
        console.error("Error submitting system config:", error);
        // Display error notification
        toast.error("Error submitting system config", {
          position: toast.POSITION.TOP_RIGHT,
        });

        // Display error notification
        if (error.response) {
          const errorMessage = error.response.data;
          toast.error(errorMessage, {
            position: toast.POSITION.TOP_RIGHT,
          });
        } else {
          toast.error("Error submitting system config", {
            position: toast.POSITION.TOP_RIGHT,
          });
        }
      });
  };

  const handleUpdateTemplate = () => {
    // Check if the updated template is not empty
    if (updatedTemplate.trim() === "") {
      console.error(
        "Updated template is empty. Please provide a non-empty template."
      );
      return;
    }

    const updatedTemplateData = {
      clientId: parseInt(clientId) || 0, // Parse clientId to an integer or default to 0
      systemId: selectedSystemId, // Add the systemId parameter
      vendorName: selectedVendor,
      vendorId: selectedVendorId,
      template: updatedTemplate,
    };


    // Send the PUT request to update the template
    axios
      .put(
        `${API_BASE_URL}${VENDORS}/${selectedVendorId}`,
        updatedTemplateData,
        { headers: header }
      )
      .then((response) => {
        // Close the template modal after updating
        setIsTemplateUpdated(true);
        setIsTemplateModalOpen(false);
        setIsToTemplateModalOpen(false);
      })
      .catch((error) => {
        console.error("Error updating template:", error);
      });
  };
  const handleUpdateToTemplate = () => {
    // Check if the updated to template is not empty
    if (toSelectedTemplate.trim() === "") {
      console.error(
        "Updated to template is empty. Please provide a non-empty template."
      );
      return;
    }

    const updatedToTemplateData = {
      clientId: parseInt(clientId) || 0, // Parse clientId to an integer or default to 0
      systemId: toSelectedSystemId, // Add the systemId parameter
      vendorName: toSelectedVendor,
      vendorId: toSelectedVendorId,
      template: toSelectedTemplate,
    };

    // Send the PUT request to update the to template
    axios
      .put(
        `${API_BASE_URL}${VENDORS}/${toSelectedVendorId}`,
        updatedToTemplateData,
        { headers: header }
      )
      .then((response) => {
        // Close the to template modal after updating
        setIsToTemplateUpdated(true);
        setIsToTemplateModalOpen(false);
      })
      .catch((error) => {
        console.error("Error updating to template:", error);
      });
  };
  const handleViewTemplate = (fromVendorTemplate, toVendorTemplate) => {
    setViewedFromVendorTemplate(fromVendorTemplate);
    setViewedToVendorTemplate(toVendorTemplate);
    setViewTemplateModalOpen(true);
  };

  const handleDeleteSystemConfig = (systemConfigId) => {
    // Send a DELETE request to the API
    axios
      .delete(
        `${API_BASE_URL}${SYSTEM_CONFIG}/${systemConfigId}`,
        {
          headers: header,
        }
      )
      .then((response) => {
        // Update the state to remove the deleted system configuration
        setSystemConfigs((prevConfigs) =>
          prevConfigs.filter((config) => config.id !== systemConfigId)
        );
      })
      .catch((error) => {
        console.error("Error deleting system config:", error);
      });
  };
  const handleEditClick = async (config) => {
    try {
      // Fetch the "Selected From Template" and "Selected To Template" from the API
      const response = await axios.get(
        `${API_BASE_URL}${SYSTEM_CONFIG}/${config.systemConfigId}`,
        { headers: header }
      );

      // Extract the "Selected From Template" and "Selected To Template" from the response
      const fromTemplate = response.data.fromVendorTemplate;
      const toTemplate = response.data.toVendorTemplate;

      // Set the templates in the state
      setSelectedTemplate(fromTemplate);
      setToSelectedTemplate(toTemplate);
      setEditingSystemConfigId(config.systemConfigId);

      // Open the combined template modal
      setIsCombinedTemplateModalOpen(true);
    } catch (error) {
      console.error("Error fetching templates:", error);
    }
  };
  const handleUpdateSystemConfig = () => {
    // Ensure editingSystemConfigId is set
    if (!editingSystemConfigId) {
      console.error("No system config ID is set for editing.");
      return;
    }

    const updatedData = {
      clientId: parseInt(clientId) || 0,
      namespaceId: parseInt(selectedNamespaceId) || 0,
      fromSystemId: selectedSystemId,
      fromVendorId: selectedVendorId,
      fromVendorTemplate: selectedTemplate,
      toSystemId: toSelectedSystemId,
      toVendorId: toSelectedVendorId,
      toVendorTemplate: toSelectedTemplate,
      serviceName: systemConfigName,
    };
    // Send the PUT request to update the system config
    axios
      .put(
        `${API_BASE_URL}${SYSTEM_CONFIG}/${editingSystemConfigId}`,
        updatedData,
        { headers: header }
      )
      .then((response) => {
        // Optionally, you can reset the editingSystemConfigId and close the modal
        setEditingSystemConfigId(null);
        setIsCombinedTemplateModalOpen(false);
      })
      .catch((error) => {
        console.error("Error updating system config:", error);
      });
  };


  const isAdminOrSuperAdmin = ["ADMIN", "SUPERADMIN"].includes(userRole);

  if (!isAdminOrSuperAdmin) {
    // If the user is not an ADMIN or SUPERADMIN, render a message or redirect
    return (
      <div className="warning-message">
        <p>
          <strong>Access Denied:</strong> You do not have permission to view
          this page.
        </p>
      </div>
    );
  }
  return (
    <div
      className="container system-config container"
      id="tableStartingStyling"
    >
      <ToastContainer />
      <h4 className="text-center pt-3">System Configuration</h4>
      <br />
      <div className="dropdown-row-container">
        {/* Client Dropdown */}
        {["SUPERADMIN"].includes(userRole) && (
          <div className="dropdown-container">
            <label htmlFor="clientDropdown" className="label">
              Client
            </label>
            <select
              id="clientDropdown"
              className="select"
              value={clientId}
              onChange={handleClientChange}
            >
              <option value="" disabled>
                Select a client
              </option>
              {clients.map((client) => (
                <option key={client.clientId} value={client.clientId}>
                  {client.spocName}
                </option>
              ))}
            </select>
          </div>
        )}

        {/* Namespace Dropdown */}
        <div className="dropdown-container">
          <label htmlFor="namespaceDropdown" className="label">
            Namespace
          </label>
          <select
            id="namespaceDropdown"
            className="select"
            value={selectedNamespaceId}
            onChange={handleNamespaceChange}
          >
            <option value="" disabled>
              Select a namespace
            </option>
            {namespaces.map((namespace) => (
              <option key={namespace.namespaceId} value={namespace.namespaceId}>
                {namespace.namespaceName}
              </option>
            ))}
          </select>
        </div>
      </div>
      <br />

      {/* from vendor from system*/}
      <div className="dropdown-row-container">
        {/* System Dropdown */}
        <div className="dropdown-container">
          <label htmlFor="systemDropdown" className="label">
            From System
          </label>
          <select
            id="systemDropdown"
            className="select"
            value={selectedSystem}
            onChange={handleFromSystemChange}
          >
            <option value="" disabled>
              Select a system
            </option>
            {systems.map((system) => (
              <option key={system.id} value={system.systemName}>
                {system.systemName}
              </option>
            ))}
          </select>
        </div>
        {/* Vendor Dropdown component */}
        <div className="dropdown-container">
          <label htmlFor="vendorDropdown" className="label">
            From Vendor
          </label>
          <select
            className="select"
            value={selectedVendor}
            onChange={handleVendorChange}
          >
            <option value="">Select a vendor</option>
            {vendorsForSystem.map((vendor) => (
              <option key={vendor.vendorId} value={vendor.vendorName}>
                {vendor.vendorName}
              </option>
            ))}
          </select>
          {isTemplateUpdated && (
            <div className="sum-system-config">
              <h5>Updated Template</h5>
              <p>{updatedTemplate}</p>
            </div>
          )}
          {isTemplateModalOpen && (
            <div className="modal">
              <div className="modal-content">
                <div className="d-flex justify-content-between align-items-center">
                  <h3>Selected From Template</h3>
                  <span
                    className="modelCancelButton"
                    onClick={() => setIsTemplateModalOpen(false)}
                  >
                    <IoCloseSharp />
                  </span>
                </div>
                <LineNumberedTextarea
                  value={selectedTemplate}
                  onChange={(updatedValue) => setUpdatedTemplate(updatedValue)}
                />

                <button onClick={handleUpdateTemplate}>Update Template</button>
              </div>
            </div>
          )}
        </div>

        {/* to vendor t0 system*/}
        {/* System Dropdown */}
        <div className="dropdown-container">
          <label htmlFor="systemDropdown" className="label">
            To System
          </label>
          <select
            id="toSystemDropdown"
            className="select"
            value={toSelectedSystem}
            onChange={handleToSystemChange}
          >
            <option value="" disabled>
              Select a system
            </option>
            {systems.map((system) => (
              <option key={system.id} value={system.systemName}>
                {system.systemName}
              </option>
            ))}
          </select>
        </div>
        {/* Vendor Dropdown component */}
        <div className="dropdown-container">
          <label htmlFor="vendorDropdown" className="label">
            To Vendor
          </label>
          <select
            className="select"
            value={toSelectedVendor}
            onChange={handleToVendorChange}
          >
            <option value="">Select a vendor</option>
            {toVendorsForSystem.map((vendor) => (
              <option key={vendor.vendorId} value={vendor.vendorName}>
                {vendor.vendorName}
              </option>
            ))}
          </select>
          {isToTemplateUpdated && (
            <div className="sum-system-config">
              <h5>Updated Template</h5>
              <p>{toSelectedTemplate}</p>
            </div>
          )}

          {isToTemplateModalOpen && (
            <div className="modal">
              <div className="modal-content">
                <div className="d-flex justify-content-between align-items-center">
                  <h3>Selected To Template</h3>
                  <span
                    className="modelCancelButton"
                    onClick={() => setIsToTemplateModalOpen(false)}
                  >
                    <IoCloseSharp />
                  </span>
                </div>
                <LineNumberedTextarea
                  value={toSelectedTemplate}
                  onChange={(updatedValue) =>
                    setToSelectedTemplate(updatedValue)
                  }
                />

                <button onClick={handleUpdateToTemplate}>
                  Update To Template
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="selected-system-config-container">
        <div className="sum-system-config ">
          <h3>Selected System Config</h3>
          <p>
            {selectedSystem && <span>From: {selectedSystem}</span>}
            {selectedVendor && <span> --> {selectedVendor}</span>}
            {toSelectedSystem && <span> To: {toSelectedSystem}</span>}
            {toSelectedVendor && <span> --> {toSelectedVendor}</span>}
          </p>
        </div>
      </div>

      <div className="centered-button-container">
        {/* Centered Submit Button */}
        <button
          className="SystemUpdateButton"
          type="submit"
          onClick={handleSubmit}
        >
          Submit
        </button>
      </div>
      {/* Display System Configurations Table */}
      <div className="system-configs-table-container">
        <h4 className="text-center mt-4">System Configurations</h4>
        <table className="table">
          <thead>
            <tr>
              <th>Serial Number</th>
              <th>Client Name</th>
              <th>Namespace Name</th>
              <th>Service Name</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {systemConfigs.map((config, index) => (
              <tr key={config.id}>
                <td>{index + 1}</td>
                <td>{config.clientName}</td>
                <td>{config.namespaceName}</td>
                <td>{config.serviceName}</td>
                <td>
                  <div className="d-flex justify-content-center align-items-center">
                    <Tooltip title="Update" arrow>
                      <div className="EditDeleteBtnStyling m-auto mx-1">
                        <EditTwoToneIcon
                          className="edit-icon namespaceIcons myEditIcons"
                          onClick={() => handleEditClick(config)}
                        />
                      </div>
                    </Tooltip>
                    <Tooltip title="Delete" arrow>
                      <div className="EditDeleteBtnStyling m-auto mx-1">
                        <DeleteTwoToneIcon
                          className="edit-icon namespaceIcons myEditIcons"
                          onClick={() =>
                            handleDeleteSystemConfig(config.systemConfigId)
                          }
                        />
                      </div>
                    </Tooltip>
                    <Tooltip title="View" arrow>
                      <div className="EditDeleteBtnStyling m-auto mx-1">
                        <VisibilityTwoToneIcon
                          className="edit-icon namespaceIcons myEditIcons"
                          onClick={() =>
                            handleViewTemplate(
                              config.fromVendorTemplate,
                              config.toVendorTemplate
                            )
                          }
                        />
                      </div>
                    </Tooltip>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
          {isCombinedTemplateModalOpen && (
            <div className="modal">
              <div className="modal-content">
                <div className="d-flex justify-content-between align-items-center">
                  <h3>Selected Templates</h3>
                  <span
                    className="modelCancelButton"
                    onClick={() => setIsCombinedTemplateModalOpen(false)}
                  >
                    <IoCloseSharp />
                  </span>
                </div>
                {/* Content for "From" template */}
                <div className="flex-container">
                  <div className="template-section">
                    <h4>Selected From Template</h4>
                    <LineNumberedTextarea
                      value={selectedTemplate}
                      onChange={(updatedValue) =>
                        setUpdatedTemplate(updatedValue)
                      }
                    />
                  </div>
                  {/* "Update Template" button */}
                  <div className="button-section">
                    <button onClick={handleUpdateSystemConfig}>
                      Update Template
                    </button>
                  </div>
                </div>
                {/* Content for "To" template */}
                <div className="flex-container">
                  <div className="template-section">
                    <h4>Selected To Template</h4>
                    <LineNumberedTextarea
                      value={toSelectedTemplate}
                      onChange={(updatedValue) =>
                        setToSelectedTemplate(updatedValue)
                      }
                    />
                  </div>
                  {/* "Update Template" button */}
                  <div className="button-section">
                    <button onClick={handleUpdateSystemConfig}>
                      Update To Template
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Popup modal for viewing templates */}
          {viewTemplateModalOpen && (
            <div className="modal">
              <div className="modal-content">
                <div className="d-flex justify-content-between align-items-center">
                  <h3>View Templates</h3>
                  <span
                    className="modelCancelButton"
                    onClick={() => setViewTemplateModalOpen(false)}
                  >
                    <IoCloseSharp />
                  </span>
                </div>
                <div>
                  <h4>From Vendor Template</h4>
                  <p>{viewedFromVendorTemplate}</p>
                </div>
                <div>
                  <h4>To Vendor Template</h4>
                  <p>{viewedToVendorTemplate}</p>
                </div>
              </div>
            </div>
          )}
        </table>
      </div>
    </div>
  );
};

export default SystemConfig;
