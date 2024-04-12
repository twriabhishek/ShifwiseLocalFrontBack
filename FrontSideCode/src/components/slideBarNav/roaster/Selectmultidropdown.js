import React, { useState, useEffect } from "react";
import { Container } from "react-bootstrap";
import Multiselect from "multiselect-react-dropdown";

function Selectmultidropdown({ onSelectionChange }) {
  const [options, setOptions] = useState([]);
  const [selectedOptions, setSelectedOptions] = useState([]); 

  const apiUrl =
    "https://api-e32.niceincontact.com/incontactapi/services/v27.0/agents?isActive=true";
  const accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjp7ImxlZ2FjeUlkIjoiQWRtaW5pc3RyYXRvciIsImlkIjoiMjAwNDcyNmItMjc5MC00N2IyLTk3MjgtZDU3NDllYmQ2ZjA0IiwibGFzdFVwZGF0ZVRpbWUiOjE2OTQ3NTE1MzAwMDAsInNlY29uZGFyeVJvbGVzIjpbXX0sInZpZXdzIjp7fSwiaWNBZ2VudElkIjoiMzkyMzA0NDYiLCJpY1NQSWQiOiIyMjY2Iiwic3ViIjoidXNlcjoxMWVlMmI4ZS03M2VmLTMxMzAtYjUxNy0wMjQyYWMxMTAwMDQiLCJpc3MiOiJodHRwczovL2F1dGgubmljZWluY29udGFjdC5jb20iLCJnaXZlbl9uYW1lIjoiTWF6YWhhcnVsIiwiYXVkIjoiMzJhOTMyOWItMThkOC00M2NkLWFhNTAtMjdmNDhjODE2Y2M4QGN4b25lIiwiaWNCVUlkIjo0NjA0OTMwLCJuYW1lIjoibWF6YWhhckB2b2ljZXJhLWFuYWx5dGljcy5jb20iLCJ0ZW5hbnRJZCI6IjExZWQyMzAwLWMyODAtOGM1MC1iZmVlLTAyNDJhYzExMDAwMyIsImZhbWlseV9uYW1lIjoiSGFxdWUiLCJ0ZW5hbnQiOiJleGF0b190ZWNobm9sb2dpZXNfcHZ0X2x0ZF9kZW1vXzY4MTA5NzE4IiwiaWNDbHVzdGVySWQiOiJFMzIiLCJpYXQiOjE2OTc0MzI2NDgsImV4cCI6MTY5NzQzNjI0OH0.RY3TqtkWby7DOv3OLpxpms1wPCEFy9FwNhxa0jAsoFJP7WLVlKR6gG1VypNKp3cb2GfdCzeFen9gH4rSxImEx3tWGko0xa4kxZE6Zfte5P38yCqilseczl6elf5sON6Dwzfd_gwnW91hJ8H9r6N5V8B4Un53P15xpYk-r2QdoXI"; // Replace with your access token

  useEffect(() => {
    // Fetching agent data from the API and extract first names
    fetch(apiUrl, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        if (data && data.agents) {
          const firstNames = data.agents.map((agent) => agent.firstName);
          setOptions(firstNames);
        }
      })
      .catch((error) => {
        console.error("Error fetching agent data:", error);
      });
  }, []);


  const handleSelectionChange = (selectedList) => {
    setSelectedOptions(selectedList);
    onSelectionChange(selectedList); 
  };

  return (
    <React.Fragment>
      <Container className="content">
        <div className="row">
          <div className="col-sm-12">
            <form className="row g-3" method="post">
              <div className="col-md-5">
                <div className="text-dark">
                  <Multiselect
                    isObject={false}
                    onSelect={handleSelectionChange} 
                    onRemove={handleSelectionChange} 
                    options={options}
                    showCheckbox
                  />
                </div>
              </div>
            </form>
          </div>
        </div>
      </Container>
    </React.Fragment>
  );
}

export default Selectmultidropdown;
