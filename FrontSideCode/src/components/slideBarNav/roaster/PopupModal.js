import React, { useState } from "react";
import "./PopupModal.css";
const teams = ["Team A", "Team B", "Team C"]; // Dummy team values
const users = [" 1", " 2", " 3"]; // Dummy user names

const PopupModal = ({ onClose }) => {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [selectedTeam, setSelectedTeam] = useState("");
  const [selectedUser, setSelectedUser] = useState("");

  const handleSubmit = () => {
    const response = {
      startDate,
      endDate,
      team: selectedTeam,
      user: selectedUser
    };
    console.log(JSON.stringify(response));
    onClose(); // Close the modal
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h2> Create Roster</h2>
        <label htmlFor="startDate">Start Date:</label>
        <input
          type="date"
          id="startDate"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
        />
        <label htmlFor="endDate">End Date:</label>
        <input
          type="date"
          id="endDate"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
        />
        <label htmlFor="team">Team:</label>
        <select
          id="team"
          value={selectedTeam}
          onChange={(e) => setSelectedTeam(e.target.value)}
        >
          <option value="">Select a team</option>
          {teams.map((team, index) => (
            <option key={index} value={team}>
              {team}
            </option>
          ))}
        </select>
        <label htmlFor="userName">Roster Name:</label>
        <select
          id="userName"
          value={selectedUser}
          onChange={(e) => setSelectedUser(e.target.value)}
        >
          <option value="">Select a Roster</option>
          {users.map((user, index) => (
            <option key={index} value={user}>
              {user}
            </option>
          ))}
        </select>
        <button onClick={handleSubmit}>Submit</button>
      </div>
    </div>
  );
};

export default PopupModal;
