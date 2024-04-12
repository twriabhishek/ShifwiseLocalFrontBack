import React from "react";
import Pagination from "react-bootstrap/Pagination";

const PageNavigation = ({
  activePage,
  totalItems,
  itemsPerPage,
  setActivePage,
}) => {
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  const handlePageClick = (page) => {
    setActivePage(page);
  };

  return (
    <Pagination
      className="justify-content-end ms-auto"
      style={{ marginRight: "20px" }}
    >
      <Pagination.Prev
        onClick={() => handlePageClick(Math.max(activePage - 1, 1))}
        disabled={activePage === 1}
      />
      {Array.from({ length: totalPages }).map((_, index) => (
        <Pagination.Item
          key={index + 1}
          active={index + 1 === activePage}
          onClick={() => handlePageClick(index + 1)}
        >
          {index + 1}
        </Pagination.Item>
      ))}
      <Pagination.Next
        onClick={() => handlePageClick(Math.min(activePage + 1, totalPages))}
        disabled={activePage === totalPages}
      />
    </Pagination>
  );
};

export default PageNavigation;
