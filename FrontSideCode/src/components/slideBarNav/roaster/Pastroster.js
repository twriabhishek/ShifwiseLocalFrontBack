import React from "react";
import jsonData from "../alldata.json";
import "./Table.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye } from "@fortawesome/free-solid-svg-icons";

const Table = ({ data }) => {
  // Define column headings dynamically
  const columns = ["rosterId", "startDate", "endDate", "team", "view"];

  return (
    <table>
      <thead>
        <tr>
          {columns.map((column, index) => (
            <th key={index}>{column}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {data.map((item, rowIndex) => (
          <tr key={rowIndex}>
            {columns.map((column, colIndex) => (
              <td key={colIndex}>
                {/* Render 'view' column as a button (you can customize this part) */}
                {column === "view" ? (
                  <button onClick={() => handleView(item)}>
                    <FontAwesomeIcon icon={faEye} />
                  </button>
                ) : (
                  item[column]
                )}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
};

function handleView(item) {
  // Handle the 'View' button click here (e.g., show a modal with details).
  console.log("View clicked for rosterId:", item.rosterId);
}

function App() {
  return (
    <div className="App">
      <Table data={jsonData} />
    </div>
  );
}

export default Table;
