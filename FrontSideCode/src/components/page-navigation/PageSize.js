import React, { useState } from "react";
import { Form } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCog } from "@fortawesome/free-solid-svg-icons";
import PropTypes from "prop-types";

const PageSize = ({
  handleItemsPerPageChange,
  itemsPerPage,
  pageSizeOptions,
}) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const toggleDropdown = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  PageSize.propTypes = {
    handleItemsPerPageChange: PropTypes.func.isRequired,
    itemsPerPage: PropTypes.number.isRequired,
    pageSizeOptions: PropTypes.array.isRequired,
  };

  return (
    <div className="d-flex align-items-center">
      <button
        style={{
          cursor: "pointer",
          marginRight: "5px",
          background: "none",
          border: "none",
        }}
        onClick={toggleDropdown}
      >
        <FontAwesomeIcon icon={faCog} />
      </button>
      <button
        style={{ cursor: "pointer", background: "none", border: "none" }}
        onClick={toggleDropdown}
      >
        Page Size
      </button>
      {isDropdownOpen && (
        <Form.Select
          className="me-3"
          onChange={(e) => handleItemsPerPageChange(Number(e.target.value))}
          value={itemsPerPage}
          style={{
            fontSize: "15px",
            padding: "5px",
            width: "50px",
            marginBottom: "0",
          }}
        >
          {pageSizeOptions.map((size) => (
            <option key={size} value={size}>
              {size}
            </option>
          ))}
        </Form.Select>
      )}
    </div>
  );
};

export default PageSize;
