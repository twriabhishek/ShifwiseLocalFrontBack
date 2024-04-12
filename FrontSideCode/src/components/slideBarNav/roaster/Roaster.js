import React, { useState } from "react";
import Select from "react-select";
import "./Roaster.css";
import Selectmultidropdown from "./Selectmultidropdown"; 
import { Container } from "react-bootstrap"; 

const App = () => {
  const [rows, setRows] = useState([
    { name: "5 Sept", members: ["Select Agent", "Select Agent", "Select Agent"] },
    { name: "6 Sept", members: ["Select Agent", "Select Agent", "Select Agent"] },
    { name: "7 Sept", members: ["Select Agent", "Select Agent", "Select Agent"] },
  ]);

  const [columns, setColumns] = useState([
    { name: "Date" },
    { name: "Morning" },
    { name: "Afternoon" },
    { name: "Evening" },
  ]);

  const handleTimingChange = (e, rowIndex) => {
    const updatedRows = [...rows];
    updatedRows[rowIndex].name = e.value;
    setRows(updatedRows);
  };

  const handleNameChange = (newValue, rowIndex, memberIndex) => {
    const updatedRows = [...rows];
    updatedRows[rowIndex].members[memberIndex] = newValue.value;
    setRows(updatedRows);
  };

  const customStyles = {
    option: (provided, state) => ({
      ...provided,
      backgroundColor: state.isSelected ? "lightblue" : "white",
    }),
  };

  return (
    <div className="App">
      <table>
        <thead>
          <tr>
            {columns.map((column, columnIndex) => (
              <th key={columnIndex}>{column.name}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td>
                <Select
                  options={[
                    { value: "5 Sept", label: "5 sept" },
                    { value: "6 Sept", label: "6 Sept" },
                    { value: "7 Sept", label: "7 Sept" },
                  ]}
                  value={{ value: row.name, label: row.name }}
                  onChange={(e) => handleTimingChange(e, rowIndex)}
                  styles={customStyles}
                />
              </td>
              {row.members.map((member, memberIndex) => (
                <td key={memberIndex}>
                  <Selectmultidropdown
                    options={['Jack', 'John', 'Jane']}
                    // Pass any other necessary props here
                  />
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default App;
