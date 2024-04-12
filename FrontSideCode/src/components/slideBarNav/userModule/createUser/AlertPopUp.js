import { Button, Modal } from "react-bootstrap";

function AlertPopUp({ showPopup, handleClosePopup }) {
  const headerStyle = {
    padding: "5px", // Set your preferred header padding
  };
  const bodyStyle = {
    padding: "10px", // Set your preferred body padding
  };

  const footerStyle = {
    padding: "5px",
    justifyContent: "center", // Set your preferred footer padding
  };

  return (
    <div className="d-flex align-items-center justify-content-center h-100">
      {/* Pop-up */}
      <Modal
        show={showPopup}
        onHide={handleClosePopup}
        dialogClassName="modal-dialog-centered modal-sm"
      >
        <Modal.Header
          closeButton={false}
          className="text-center"
          style={headerStyle}
        >
          <Modal.Title>Update Your Profile</Modal.Title>
        </Modal.Header>
        <Modal.Body style={bodyStyle}>
          <p>Please configure CMS and update your profile.</p>
        </Modal.Body>
        <Modal.Footer style={footerStyle}>
          <Button
            variant="primary"
            onClick={handleClosePopup}
            className="text-center"
          >
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}

export default AlertPopUp;
