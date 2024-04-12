import React, { useState, useEffect } from "react";
import ShiftData from "../shift.json";
import JsonData from "../data.json"
import Selectmultidropdown from "./Selectmultidropdown";
import axios from 'axios';

function JsonDataDisplay() {
  const [shifts, setShifts] = useState([]);
  const [agentData, setAgentData] = useState([]); // State to store agent data
  const [selectedAgents, setSelectedAgents] = useState({});

  useEffect(() => {
    // Fetch and set the shifts from shift.json
    setShifts(ShiftData);

    // Fetch agent data from the API
    const apiUrl =
      "https://api-e32.niceincontact.com/incontactapi/services/v27.0/agents?isActive=true";
    const accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjp7ImxlZ2FjeUlkIjoiQWRtaW5pc3RyYXRvciIsImlkIjoiMjAwNDcyNmItMjc5MC00N2IyLTk3MjgtZDU3NDllYmQ2ZjA0IiwibGFzdFVwZGF0ZVRpbWUiOjE2OTQ1OTM0MjQwMDAsInNlY29uZGFyeVJvbGVzIjpbXX0sInZpZXdzIjp7fSwiaWNBZ2VudElkIjoiMzkyMzA0NDYiLCJpY1NQSWQiOiIyMjY2Iiwic3ViIjoidXNlcjoxMWVlMmI4ZS03M2VmLTMxMzAtYjUxNy0wMjQyYWMxMTAwMDQiLCJpc3MiOiJodHRwczovL2F1dGgubmljZWluY29udGFjdC5jb20iLCJnaXZlbl9uYW1lIjoiTWF6YWhhcnVsIiwiYXVkIjoiMzJhOTMyOWItMThkOC00M2NkLWFhNTAtMjdmNDhjODE2Y2M4QGN4b25lIiwiaWNCVUlkIjo0NjA0OTMwLCJuYW1lIjoibWF6YWhhckB2b2ljZXJhLWFuYWx5dGljcy5jb20iLCJ0ZW5hbnRJZCI6IjExZWQyMzAwLWMyODAtOGM1MC1iZmVlLTAyNDJhYzExMDAwMyIsImZhbWlseV9uYW1lIjoiSGFxdWUiLCJ0ZW5hbnQiOiJleGF0b190ZWNobm9sb2dpZXNfcHZ0X2x0ZF9kZW1vXzY4MTA5NzE4IiwiaWNDbHVzdGVySWQiOiJFMzIiLCJpYXQiOjE2OTQ2MDIxODcsImV4cCI6MTY5NDYwNTc4N30.QiCZXf7qwapmuVY9IvavPmSd1fsteFOKdL5MdJm6mr3lIfIHMT-67Rs4xJIFtMnMYom_05OX4fCMqFAfD004h4dcZ-thPc8rPjnPkKkf4LHuwWKLtTu3xj04Yc-J8nSts_Lt3oI0MFa_UhQ3fouIMWyK2nmzVAsNBThU6d_io0k"; // Replace with your access token

    fetch(apiUrl, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        if (data && data.agents) {
          setAgentData(data.agents);
        }
      })
      .catch((error) => {
        console.error("Error fetching agent data:", error);
      });
  }, []);

  const handleAgentSelection = (shiftName, date, selectedOptions) => {
    setSelectedAgents((prevSelectedAgents) => {
      const updatedSelectedAgents = { ...prevSelectedAgents };
      updatedSelectedAgents[date] = updatedSelectedAgents[date] || {};
      updatedSelectedAgents[date][shiftName] = selectedOptions;
      return updatedSelectedAgents;
    });
  };

  const handleRowSubmit = (info) => {
    const rowDetails = {
      date: info.date,
    };
  
    const selectedAgentsForRow = selectedAgents[info.date] || {};
  
    const selectedAgentsInfo = shifts.map((shift) => ({
      shiftName: shift.shiftName,
      agents: selectedAgentsForRow[shift.shiftName] || [],
    }));
  
    // Filter out rows with no selected agents
    const filteredAgentsInfo = selectedAgentsInfo.filter(
      (shift) => shift.agents.length > 0
    );
  
    if (filteredAgentsInfo.length > 0) {
      const submissionData = {
        rowDetails,
        selectedAgents: filteredAgentsInfo,
      };
  
      // Send the JSON data to the API
      const data1 = JSON.parse('{"id": 6, "date": "04-09-2023", "selectedAgents": [{"saId": 11,"shiftName": "Morning","agents": ["Nimisha" ] }, { "saId": 12,"shiftName": "Afternoon","agents": ["Sandeep"]} ]}')
      axios.post("https://9920-106-51-77-170.ngrok-free.app/data/saveShiftAndAgent",data1) .then((response) => {
       
          console.log("Data submitted successfully.", response.data);
       
        
      })
      .catch((error) => {
        console.error("Error sending data to the API:", error);
      });
  } else {
    console.log("No agents selected for this row.");
  }
};
  //     fetch("https://9920-106-51-77-170.ngrok-free.app/data/saveShiftAndAgent", {
  //       mode: 'no-cors',
  //       method: "POST",
  //       // headers: {
  //       //   "Content-Type": "application/json",
  //       // },
  //       body: (data1),
  //     })
  //       .then((response) => {
  //         if (response.ok) {
  //           console.log("Data submitted successfully.");
  //         } else {
  //           console.error("Error submitting data to the API.");
  //         }
  //       })
  //       .catch((error) => {
  //         console.error("Error sending data to the API:", error);
  //       });
  //   } else {
  //     console.log("No agents selected for this row.");
  //   }
  // };
  
  const DisplayData = JsonData.map((info) => {
    return (
      <tr key={info.id}>
        <td>{info.date}</td>
        {shifts.map((shift) => (
          <td key={shift.id}>
            <Selectmultidropdown
              options={agentData} // Use agentData from the API
              selectedOptions={
                selectedAgents[info.date] &&
                selectedAgents[info.date][shift.shiftName]
              }
              onSelectionChange={(selectedOptions) => {
                handleAgentSelection(shift.shiftName, info.date, selectedOptions);
              }}
            />
          </td>
        ))}
        <td>
          <button className="btn btn-success submit-button" onClick={() => handleRowSubmit(info)}>Submit</button>
        </td>
      </tr>
    );
  });

  return (
    <div>
      <table>
        <thead>
          <tr>
            <th>Date</th>
            {shifts.map((shift) => (
              <th key={shift.id}>{shift.shiftName}</th>
            ))}
            <th>Operations</th>
          </tr>
        </thead>
        <tbody>{DisplayData}</tbody>
      </table>
    </div>
  );
}

export default JsonDataDisplay;
