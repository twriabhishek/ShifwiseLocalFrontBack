import React, { useState, useEffect } from "react";
import axios from "axios";
import Button from "@mui/joy/Button";
import Divider from "@mui/joy/Divider";
import DialogTitle from "@mui/joy/DialogTitle";
import DialogContent from "@mui/joy/DialogContent";
import DialogActions from "@mui/joy/DialogActions";
import Modal from "@mui/joy/Modal";
import ModalDialog from "@mui/joy/ModalDialog";
import WarningRoundedIcon from "@mui/icons-material/WarningRounded";
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import Tooltip from "@mui/material/Tooltip";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Draggable from 'react-draggable';
import { API_BASE_URL, BUSINESS_UNIT, CLIENTLIST, GROUP, PROCESS_UNIT, QUEUES, SKILLS, SKILL_WEIGHTAGES, SUB_PROCESSES, TEAMS, TENANT_NAMESPACES } from "../../constant-API/constants";

const userRole = localStorage.getItem("roles");
const isAdminOrSuperAdmin = ["SUPERADMIN"].includes(userRole);
console.log(isAdminOrSuperAdmin);


const PopupConfirmation = ({ open, setOpen, handleToggle }) => {
  return (
    <Modal open={open} onClose={() => setOpen(false)}>
      <ModalDialog variant="outlined" role="alertdialog">
        <DialogTitle>
          <WarningRoundedIcon />
          Confirmation
        </DialogTitle>
        <Divider />
        <DialogContent>
          Are you sure you want to change the active state?
        </DialogContent>
        <DialogActions>
          <Button variant="solid" color="danger" onClick={handleToggle}>
            Change
          </Button>
          <Button
            variant="plain"
            color="neutral"
            onClick={() => setOpen(false)}
          >
            Cancel
          </Button>
        </DialogActions>
      </ModalDialog>
    </Modal>
  );
};

const Form = () => {
  const [businessUnits, setBusinessUnits] = useState([]);
  const [processUnits, setProcessUnits] = useState([]);
  const [subProcesses, setSubProcesses] = useState([]);
  const [teams, setTeams] = useState([]);
  const [groups, setGroups] = useState([]);
  const [queues, setQueues] = useState([]);
  const [skills, setSkills] = useState([]);
  const [skillWeightages, setSkillWeightages] = useState([]);
  const [selectedNamespace, setSelectedNamespace] = useState("");

  const [businessUnitId, setBusinessUnitId] = useState(0);
  const [processUnitId, setProcessUnitId] = useState(0);
  const [subProcessId, setSubProcessId] = useState(0);
  const [teamId, setTeamId] = useState(0);
  const [groupId, setGroupId] = useState(0);
  const [queueId, setQueueId] = useState(0);
  const [skillId, setSkillId] = useState(0);
  const [skillWeightageId, setSkillWeightageId] = useState(0);

  const [clients, setClients] = useState([]);
  const [selectedClient, setSelectedClient] = useState("");
  const [clientId, setClientId] = useState("");
  const [namespaceData, setNamespaceData] = useState([]);
  const [showModal, setShowModal] = useState(false);
  // const [updatedNamespaceName, setUpdatedNamespaceName] = useState("");
  // const [selectedNamespaceId, setSelectedNamespaceId] = useState("");

  const [confirmationOpen, setConfirmationOpen] = React.useState(false);
  const [currentNamespaceId, setCurrentNamespaceId] = React.useState(null);
  const [currentNamespaceStatus, setCurrentNamespaceStatus] =
    React.useState(false);
  const [currentNamespaceName, setCurrentNamespaceName] = React.useState(null);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [modifiedNamespaceName, setModifiedNamespaceName] = useState("");

  // Take the token 
  const [isAdminOrSuperAdmin, setIsAdminOrSuperAdmin] = useState(false)
  useEffect(() => {
    const userRole = localStorage.getItem('roles');
    const isAdminOrSuperAdmin = ["SUPERADMIN"].includes(userRole);
    setIsAdminOrSuperAdmin(isAdminOrSuperAdmin);
  }, [])

  const [editModalData, setEditModalData] = useState({
    clientId: "",
    businessUnitId: "",
    processUnitId: "",
    subProcessId: "",
    teamId: "",
    groupId: "",
    queueId: "",
    skillId: "",
    skillWeightageId: "",
  });

  const handleEditModalOpen = (namespaceId) => {
    const namespace = namespaceData.find((namespace) => namespace.namespaceId === namespaceId);

    if (namespace) {
      const clientId = namespace.clientId ? namespace.clientId.toString() : "";
      const businessUnitId = namespace.businessUnitId ? namespace.businessUnitId.toString() : "";
      const processUnitId = namespace.processUnitId ? namespace.processUnitId.toString() : "";
      const subProcessId = namespace.subProcessId ? namespace.subProcessId.toString() : "";
      const teamId = namespace.teamId ? namespace.teamId.toString() : "";
      const groupId = namespace.groupId ? namespace.groupId.toString() : "";
      const queueId = namespace.queueId ? namespace.queueId.toString() : "";
      const skillId = namespace.skillId ? namespace.skillId.toString() : "";
      const skillWeightageId = namespace.skillWeightageId ? namespace.skillWeightageId.toString() : "";

      setEditModalData({
        ...editModalData,
        clientId,
        businessUnitId,
        processUnitId,
        subProcessId,
        teamId,
        groupId,
        queueId,
        skillId,
        skillWeightageId,
      });

      setEditModalOpen(true);
      setCurrentNamespaceId(namespaceId);
    } else {
      console.error("Namespace not found with id:", namespaceId);
    }
  };

  const handleEditSubmit = async () => {
    try {
      // Construct the namespaceName using the selected values
      const namespaceName = `${editModalData.clientId ? clients.find(client => client.clientId === parseInt(editModalData.clientId))?.spocName + "." : ""}`
        + `${editModalData.businessUnitId ? businessUnits.find(unit => unit.businessUnitId === parseInt(editModalData.businessUnitId))?.businessUnitName + "." : ""}`
        + `${editModalData.processUnitId ? processUnits.find(unit => unit.processUnitId === parseInt(editModalData.processUnitId))?.processUnitName + "." : ""}`
        + `${editModalData.subProcessId ? subProcesses.find(unit => unit.subProcessId === parseInt(editModalData.subProcessId))?.subProcessName + "." : ""}`
        + `${editModalData.teamId ? teams.find(unit => unit.teamId === parseInt(editModalData.teamId))?.teamName + "." : ""}`
        + `${editModalData.groupId ? groups.find(unit => unit.groupId === parseInt(editModalData.groupId))?.groupName + "." : ""}`
        + `${editModalData.queueId ? queues.find(unit => unit.queueId === parseInt(editModalData.queueId))?.queueName + "." : ""}`
        + `${editModalData.skillId ? skills.find(unit => unit.skillId === parseInt(editModalData.skillId))?.skillName + "." : ""}`
        + `${editModalData.skillWeightageId ? skillWeightages.find(unit => unit.skillWeightageId === parseInt(editModalData.skillWeightageId))?.skillWeightageName : ""}`;

      // Construct the payload with the updated data
      const payload = {
        clientId: parseInt(editModalData.clientId),
        businessUnitId: parseInt(editModalData.businessUnitId),
        processUnitId: parseInt(editModalData.processUnitId),
        subProcessId: parseInt(editModalData.subProcessId),
        teamId: parseInt(editModalData.teamId),
        groupId: parseInt(editModalData.groupId),
        queueId: parseInt(editModalData.queueId),
        skillId: parseInt(editModalData.skillId),
        skillWeightageId: parseInt(editModalData.skillWeightageId),
        namespaceId: 0,
        namespaceName: namespaceName,
        active: currentNamespaceStatus,
      };
      
      // Send the PUT request
      const apiUrl = `${API_BASE_URL}${TENANT_NAMESPACES}/${currentNamespaceId}`;
      const response = await axios.put(apiUrl, payload, { headers: header });

      // Close the edit modal
      setEditModalOpen(false);
    } catch (error) {
      console.error("Error updating namespace:", error);
    }
  };


  const handleConfirmationOpen = (
    namespaceId,
    currentActiveStatus,
    namespaceName
  ) => {
    setCurrentNamespaceId(namespaceId);
    setCurrentNamespaceStatus(currentActiveStatus);
    setCurrentNamespaceName(namespaceName);
    setConfirmationOpen(true);
  };

  const handleToggleConfirmation = () => {
    handleToggle(
      currentNamespaceId,
      currentNamespaceStatus,
      currentNamespaceName
    );
    setConfirmationOpen(false);
  };

  const handleToggles = (namespaceId, currentActiveStatus, namespaceName) => {
    handleConfirmationOpen(namespaceId, currentActiveStatus, namespaceName);
  };
  const header = {
    Authorization: localStorage.getItem("token"),
  };
  const handleDelete = async (namespaceId) => {
    try {
      const apiUrl = `${API_BASE_URL}${TENANT_NAMESPACES}/${namespaceId}`;
      await axios.delete(apiUrl, { headers: header });

      const updatedNamespaceData = namespaceData.filter(
        (namespace) => namespace.namespaceId !== namespaceId
      );
      setNamespaceData(updatedNamespaceData);
    } catch (error) {
      console.error("Error deleting namespace:", error);
    }
  };

  const handleSubmission = async () => {
    if (!showModal) {
      const parsedClientId = parseInt(clientId);
      const payload = {
        clientId: !isNaN(parsedClientId) ? parsedClientId : 0,
        businessUnitId: parseInt(businessUnitId),
        processUnitId: parseInt(processUnitId),
        subProcessId: parseInt(subProcessId),
        teamId: parseInt(teamId),
        groupId: parseInt(groupId),
        queueId: parseInt(queueId),
        skillId: parseInt(skillId),
        skillWeightageId: parseInt(skillWeightageId),
        namespaceId: 0,
        namespaceName: selectedNamespace,
        active: isActive,
      };
      console.log("Payload being sent:", payload);
      try {
        const response = await axios.post(
          `${API_BASE_URL}${TENANT_NAMESPACES}`,
          payload,
          { headers: header }
        );
        console.log("API response:", response.data);
        console.log("Data Successfully Submit");
        toast.success('Successfully Saved', { position: toast.POSITION.TOP_RIGHT });
        setClientId('');
        setBusinessUnitId('');
        setProcessUnitId('')
        setSubProcessId('')
        setTeamId('')
        setGroupId('')
        setQueueId('');
        setSkillId('')
        setSkillWeightageId('')
      } catch (error) {
        console.error("Error sending data:", error);
      }
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const fetchBusinessUnits = await axios.get(
          `${API_BASE_URL}${BUSINESS_UNIT}`,
          { headers: header }
        );
        setBusinessUnits(fetchBusinessUnits.data);

        const fetchProcessUnits = await axios.get(`${API_BASE_URL}${PROCESS_UNIT}`,
          { headers: header }
        );
        setProcessUnits(fetchProcessUnits.data);

        const fetchSubProcesses = await axios.get(
          `${API_BASE_URL}${SUB_PROCESSES}`,
          { headers: header }
        );
        setSubProcesses(fetchSubProcesses.data);

        const fetchTeams = await axios.get(`${API_BASE_URL}${TEAMS}`, { headers: header });
        setTeams(fetchTeams.data);

        const fetchGroups = await axios.get(`${API_BASE_URL}${GROUP}`,
          { headers: header }
        );
        setGroups(fetchGroups.data);

        const fetchQueues = await axios.get(`${API_BASE_URL}${QUEUES}`,
          { headers: header }
        );
        setQueues(fetchQueues.data);

        const fetchSkills = await axios.get(`${API_BASE_URL}${SKILLS}`,
          { headers: header }
        );
        setSkills(fetchSkills.data);

        const fetchSkillWeightages = await axios.get(`${API_BASE_URL}${SKILL_WEIGHTAGES}`, { headers: header }
        );
        setSkillWeightages(fetchSkillWeightages.data);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchData();
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    handleSubmission();
  };

  const [businessUnit, setBusinessUnit] = useState("");
  const [processUnit, setProcessUnit] = useState("");
  const [subProcess, setSubProcess] = useState("");
  const [team, setTeam] = useState("");
  const [group, setGroup] = useState("");
  const [queue, setQueue] = useState("");
  const [skill, setSkill] = useState("");
  const [skillWeightage, setSkillWeightage] = useState("");
  const [isActive, setIsActive] = useState(false);

  const handleToggle = async (
    namespaceId,
    currentActiveStatus,
    namespaceName
  ) => {
    try {

      const updatedNamespaceData = namespaceData.map((namespace) => {
        if (namespace.namespaceId === namespaceId) {
          return {
            ...namespace,
            active: !currentActiveStatus,
          };
        }
        return namespace;
      });
      setNamespaceData(updatedNamespaceData);

      const apiUrl = `${API_BASE_URL}${TENANT_NAMESPACES}/${namespaceId}`;
      const payload = {
        active: !currentActiveStatus,
        namespaceId: namespaceId,
        namespaceName: namespaceName,
      };
      console.log("Payload being sent:", payload);
      await axios.put(apiUrl, payload, { headers: header });
    } catch (error) {
      console.error("Error updating namespace active status:", error);
    }
  };

  const handleToggleSwitch = (e) => {
    setIsActive(e.target.checked);
    const response = {
      isActive: e.target.checked ? "True" : "False",
    };
    console.log(response);
  };

  const handleBusinessUnitChange = (e) => {
    const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    setBusinessUnitId(parseInt(selectedId));
    setBusinessUnit(selectedName);
  };

  const handleProcessUnitChange = (e) => {
    const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    setProcessUnitId(parseInt(selectedId));
    setProcessUnit(selectedName);
  };

  const handleSubProcessChange = (e) => {
    const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    setSubProcessId(parseInt(selectedId));
    setSubProcess(selectedName);
  };

  const handleTeamChange = (e) => {
    const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    setTeamId(parseInt(selectedId));
    setTeam(selectedName);
  };

  const handleGroupChange = (e) => {
    const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    setGroupId(parseInt(selectedId));
    setGroup(selectedName);
  };

  const handleQueueChange = (e) => {
    const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    setQueueId(parseInt(selectedId));
    setQueue(selectedName);
  };

  const handleSkillChange = (e) => {
    const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    setSkillId(parseInt(selectedId));
    setSkill(selectedName);
  };

  const handleSkillWeightageChange = (e) => {
    const selectedId = e.target.value;
    const selectedName = e.target.options[e.target.selectedIndex].text;
    setSkillWeightageId(parseInt(selectedId));
    setSkillWeightage(selectedName);
  };

  const handleClientChange = (e) => {
    const selectedClientId = e.target.value;
    setClientId(selectedClientId);
    setSelectedClient(e.target.options[e.target.selectedIndex].text);
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(
          `${API_BASE_URL}${CLIENTLIST}`,
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
    const namespace = [
      selectedClient,
      businessUnit,
      processUnit,
      subProcess,
      team,
      group,
      queue,
      skill,
      skillWeightage,
    ]
      .filter(Boolean)
      .join(".");
    setSelectedNamespace(namespace);
  }, [
    selectedClient,
    businessUnit,
    processUnit,
    subProcess,
    team,
    group,
    queue,
    skill,
    skillWeightage,
  ]);



  useEffect(() => {
    const fetchNamespaceData = async () => {
      try {
        const response = await axios.get(
          `${API_BASE_URL}${TENANT_NAMESPACES}`,
          { headers: header }
        );
        setNamespaceData(response.data);
      } catch (error) {
        console.error("Error fetching namespace data:", error);
      }
    };
    fetchNamespaceData();
  }, []);

  return (
    <form onSubmit={handleSubmit}>
      <ToastContainer />
      <h4 className="pt-3 text-center">Namespace</h4>
      <div className="toggle-container">
        <input
          type="checkbox"
          className="switch"
          onChange={handleToggleSwitch}
          checked={isActive}
        />
      </div>

      <div className="row px-3 pb-3">
        {isAdminOrSuperAdmin && (
          <div className="col-12 col-md-4 col-lg-4">
            <label className="label">Client:</label>
            <select
              className="select"
              value={clientId}
              onChange={handleClientChange}
            >
              <option value="">Select Client</option>
              {clients.map((client) => (
                <option key={client.clientId} value={client.clientId}>
                  {client.spocName}
                </option>
              ))}
            </select>
          </div>
        )}

        <div className={`${isAdminOrSuperAdmin ? 'col-12 col-md-4 col-lg-4' : 'col-12 col-md-6 col-lg-6'}`}>
          <label className="label">Business Unit:</label>
          <select
            className="select"
            value={businessUnitId}
            onChange={handleBusinessUnitChange}
          >
            <option value="">Select Business Unit</option>
            {businessUnits.map((unit) => (
              <option key={unit.businessUnitId} value={unit.businessUnitId}>
                {unit.businessUnitName}
              </option>
            ))}
          </select>
        </div>

        <div className={`${isAdminOrSuperAdmin ? 'col-12 col-md-4 col-lg-4' : 'col-12 col-md-6 col-lg-6'}`}>
          <label className="label">Process Unit:</label>
          <select
            className="select"
            value={processUnitId}
            onChange={handleProcessUnitChange}
          >
            <option value="">Select Process Unit</option>
            {processUnits.map((unit) => (
              <option key={unit.processUnitId} value={unit.processUnitId}>
                {unit.processUnitName}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="row px-3 pb-3">
        <div className="col-12 col-md-4 col-lg-4">
          <label className="label">Sub Process:</label>
          <select
            className="select"
            value={subProcessId}
            onChange={handleSubProcessChange}
          >
            <option value="">Select Sub Process</option>
            {subProcesses.map((unit) => (
              <option key={unit.subProcessId} value={unit.subProcessId}>
                {unit.subProcessName}
              </option>
            ))}
          </select>
        </div>

        <div className="col-12 col-md-4 col-lg-4">
          <label className="label">Team:</label>
          <select className="select" value={teamId} onChange={handleTeamChange}>
            <option value="">Select Team</option>
            {teams.map((unit) => (
              <option key={unit.teamId} value={unit.teamId}>
                {unit.teamName}
              </option>
            ))}
          </select>
        </div>

        <div className="col-12 col-md-4 col-lg-4">
          <label className="label">Group:</label>
          <select
            className="select"
            value={groupId}
            onChange={handleGroupChange}
          >
            <option value="">Select Group</option>
            {groups.map((unit) => (
              <option key={unit.groupId} value={unit.groupId}>
                {unit.groupName}
              </option>
            ))}
          </select>
        </div>
      </div>
      <div className="row px-3">
        <div className="col-12 col-md-4 col-lg-4">
          <label className="label">Queue:</label>
          <select
            className="select"
            value={queueId}
            onChange={handleQueueChange}
          >
            <option value="">Select Queue</option>
            {queues.map((unit) => (
              <option key={unit.queueId} value={unit.queueId}>
                {unit.queueName}
              </option>
            ))}
          </select>
        </div>

        <div className="col-12 col-md-4 col-lg-4">
          <label className="label">Skill:</label>
          <select
            className="select"
            value={skillId}
            onChange={handleSkillChange}
          >
            <option value="">Select Skill</option>
            {skills.map((unit) => (
              <option key={unit.skillId} value={unit.skillId}>
                {unit.skillName}
              </option>
            ))}
          </select>
        </div>

        <div className="col-12 col-md-4 col-lg-4">
          <label className="label">Skill Weightage:</label>
          <select
            className="select"
            value={skillWeightageId}
            onChange={handleSkillWeightageChange}
          >
            <option value="">Select Skill Weightage</option>
            {skillWeightages.map((unit) => (
              <option key={unit.skillWeightageId} value={unit.skillWeightageId}>
                {unit.skillWeightageName}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="button-container mt-3">
        <button
          className="btn buttonStyling"
          type="submit"
          onClick={handleSubmit}
        >
          Submit
        </button>
      </div>
      <div className="container text-centers">
        <div className="summary">
          <h4 className="text-center">Selected Namespace</h4>
          <p>{selectedNamespace}</p>
        </div>
      </div>

      {/* Edit Modal */}
      <Draggable>
        <Modal open={editModalOpen} onClose={() => setEditModalOpen(false)}>
          <ModalDialog
            variant="outlined"
            role="dialog"
            style={{
              width: "1000px", // Adjust the width as needed
              height: "500px", // Set the height for a square modal
              display: "flex",
              flexDirection: "column",
              justifyContent: "center",
            }}
          >
            <div className="d-flex justify-content-between">
              <DialogTitle>Edit Namespace</DialogTitle>
            </div>
            <Divider />
            <DialogContent>
              <div className="row nameSpaceEdit">
                {isAdminOrSuperAdmin && (
                  <>
                    <div className="col-12 col-md-3 col-lg-3 ">
                      <label className="label">Client:</label>
                      <select
                        className="select"
                        value={editModalData.clientId}
                        onChange={(e) =>
                          setEditModalData({
                            ...editModalData,
                            clientId: e.target.value,
                          })
                        }
                      >
                        <option value="">Select Client</option>
                        {clients.map((client) => (
                          <option key={client.clientId} value={client.clientId}>
                            {client.spocName}
                          </option>
                        ))}
                      </select>
                    </div>
                  </>
                )}

                <div className={`${isAdminOrSuperAdmin ? "col-12 col-md-4 col-lg-4" : "col-6 col-md-6 col-lg-6"}`}>
                  <label className="label">Business Unit:</label>
                  <select
                    className="select"
                    value={editModalData.businessUnitId}
                    onChange={(e) =>
                      setEditModalData({
                        ...editModalData,
                        businessUnitId: e.target.value,
                      })
                    }
                  >
                    <option value="">Select Business Unit</option>
                    {businessUnits.map((unit) => (
                      <option
                        key={unit.businessUnitId}
                        value={unit.businessUnitId}
                      >
                        {unit.businessUnitName}
                      </option>
                    ))}
                  </select>
                </div>
                <div className={`${isAdminOrSuperAdmin ? "col-12 col-md-4 col-lg-4" : "col-6 col-md-6 col-lg-6"}`}>
                  <label className="label">Process Unit:</label>
                  <select
                    className="select"
                    value={editModalData.processUnitId}
                    onChange={(e) =>
                      setEditModalData({
                        ...editModalData,
                        processUnitId: e.target.value,
                      })
                    }
                  >
                    <option value="">Select Process Unit</option>
                    {processUnits.map((unit) => (
                      <option key={unit.processUnitId} value={unit.processUnitId}>
                        {unit.processUnitName}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="row nameSpaceEdit">
                <div className="col-12 col-md-4 col-lg-4">
                  <label className="label">Sub Process:</label>
                  <select
                    className="select"
                    value={editModalData.subProcessId}
                    onChange={(e) =>
                      setEditModalData({
                        ...editModalData,
                        subProcessId: e.target.value,
                      })
                    }
                  >
                    <option value="">Select Sub Process</option>
                    {subProcesses.map((unit) => (
                      <option key={unit.subProcessId} value={unit.subProcessId}>
                        {unit.subProcessName}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-12 col-md-4 col-lg-4">
                  <label className="label">Team:</label>
                  <select
                    className="select"
                    value={editModalData.teamId}
                    onChange={(e) =>
                      setEditModalData({
                        ...editModalData,
                        teamId: e.target.value,
                      })
                    }
                  >
                    <option value="">Select Team</option>
                    {teams.map((unit) => (
                      <option key={unit.teamId} value={unit.teamId}>
                        {unit.teamName}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-12 col-md-4 col-lg-4">
                  <label className="label">Group:</label>
                  <select
                    className="select"
                    value={editModalData.groupId}
                    onChange={(e) =>
                      setEditModalData({
                        ...editModalData,
                        groupId: e.target.value,
                      })
                    }
                  >
                    <option value="">Select Group</option>
                    {groups.map((unit) => (
                      <option key={unit.groupId} value={unit.groupId}>
                        {unit.groupName}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="row nameSpaceEdit">
                <div className="col-12 col-md-4 col-lg-4">
                  <label className="label">Queue:</label>
                  <select
                    className="select"
                    value={editModalData.queueId}
                    onChange={(e) =>
                      setEditModalData({
                        ...editModalData,
                        queueId: e.target.value,
                      })
                    }
                  >
                    <option value="">Select Queue</option>
                    {queues.map((unit) => (
                      <option key={unit.queueId} value={unit.queueId}>
                        {unit.queueName}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-12 col-md-4 col-lg-4">
                  <label className="label">Skill:</label>
                  <select
                    className="select"
                    value={editModalData.skillId}
                    onChange={(e) =>
                      setEditModalData({
                        ...editModalData,
                        skillId: e.target.value,
                      })
                    }
                  >
                    <option value="">Select Skill</option>
                    {skills.map((unit) => (
                      <option key={unit.skillId} value={unit.skillId}>
                        {unit.skillName}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-12 col-md-4 col-lg-4">
                  <label className="label">Skill Weightage:</label>
                  <select
                    className="select"
                    value={editModalData.skillWeightageId}
                    onChange={(e) =>
                      setEditModalData({
                        ...editModalData,
                        skillWeightageId: e.target.value,
                      })
                    }
                  >
                    <option value="">Select Skill Weightage</option>
                    {skillWeightages.map((unit) => (
                      <option
                        key={unit.skillWeightageId}
                        value={unit.skillWeightageId}
                      >
                        {unit.skillWeightageName}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div style={{ marginBottom: "10px" }}>
                <h4>Modified Namespace</h4>
                <p>
                  {`${editModalData.clientId ? clients.find(client => client.clientId === parseInt(editModalData.clientId))?.spocName + "." : ""}`}
                  {`${editModalData.businessUnitId ? businessUnits.find(unit => unit.businessUnitId === parseInt(editModalData.businessUnitId))?.businessUnitName + "." : ""}`}
                  {`${editModalData.processUnitId ? processUnits.find(unit => unit.processUnitId === parseInt(editModalData.processUnitId))?.processUnitName + "." : ""}`}
                  {`${editModalData.subProcessId ? subProcesses.find(unit => unit.subProcessId === parseInt(editModalData.subProcessId))?.subProcessName + "." : ""}`}
                  {`${editModalData.teamId ? teams.find(unit => unit.teamId === parseInt(editModalData.teamId))?.teamName + "." : ""}`}
                  {`${editModalData.groupId ? groups.find(unit => unit.groupId === parseInt(editModalData.groupId))?.groupName + "." : ""}`}
                  {`${editModalData.queueId ? queues.find(unit => unit.queueId === parseInt(editModalData.queueId))?.queueName + "." : ""}`}
                  {`${editModalData.skillId ? skills.find(unit => unit.skillId === parseInt(editModalData.skillId))?.skillName + "." : ""}`}
                  {`${editModalData.skillWeightageId ? skillWeightages.find(unit => unit.skillWeightageId === parseInt(editModalData.skillWeightageId))?.skillWeightageName : ""}`}
                </p>

              </div>
            </DialogContent>
            <DialogActions>
              <Button variant="solid" color="primary" onClick={handleEditSubmit}>
                Update
              </Button>
              <Button
                variant="plain"
                color="neutral"
                onClick={() => setEditModalOpen(false)}
              >
                Cancel
              </Button>
            </DialogActions>
          </ModalDialog>
        </Modal>
      </Draggable>
      <div className="container table_overflow">
        <table className="namespace-table">
          <thead>
            <tr>
              <th>Serial Number</th>
              <th>Namespace Id</th>
              <th>Namespace Name</th>
              <th>Active</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {namespaceData.map((namespace, index) => (
              <tr key={namespace.namespaceId}>
                <td>{index + 1}</td>
                <td>{namespace.namespaceId}</td>
                <td>{namespace.namespaceName}</td>
                <td>
                  <PopupConfirmation
                    open={confirmationOpen}
                    setOpen={setConfirmationOpen}
                    handleToggle={handleToggleConfirmation}
                  />
                  <input
                    type="checkbox"
                    checked={namespace.active}
                    onChange={() =>
                      handleToggles(
                        namespace.namespaceId,
                        namespace.active,
                        namespace.namespaceName
                      )
                    }
                  />
                </td>
                <td className="d-flex">
                  <Tooltip title="Update" arrow>
                    <div className="EditDeleteBtnStyling m-auto">
                      <EditTwoToneIcon
                        className="edit-icon namespaceIcons myEditIcons"
                        onClick={() =>
                          handleEditModalOpen(namespace.namespaceId)
                        }
                      />
                    </div>
                  </Tooltip>
                  <Tooltip title="Delete" arrow>
                    <div className="EditDeleteBtnStyling m-auto">
                      <DeleteTwoToneIcon
                        className="delete-icon namespaceIcons myEditIcons"
                        onClick={() => handleDelete(namespace.namespaceId)}
                      />
                    </div>
                  </Tooltip>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </form>
  );
};

export default Form;