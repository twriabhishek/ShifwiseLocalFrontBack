import React, { useState } from 'react'
import { Button, Modal } from 'react-bootstrap';

function EditScheduler({ modalEdit, setModalEdit, selectedSchedulerId }) {
  const [show, setShow] = useState(false);

  const handleClose = () => setModalEdit(false);
  const handleShow = () => setShow(true);

  return (
    <>

      <Modal show={modalEdit} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Edit Section</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="container">
            <div className="row d-flex justify-content-between">
              <div className="col-md-6 col-lg-6 col-6">
                <label htmlFor="">Client Name</label>
                <select class="form-select" name="scheduleClientId" >
                  <option selected>Select Client Name</option>
                  
                </select>
              </div>

              <div className="col-md-6 col-lg-6 col-6">
                <label htmlFor="">Namespace</label>
                <select name="scheduleNamespaceId"  class="form-select" aria-label="Default select example">
                  <option selected>Select Namespace</option>
                  
                </select>
              </div>
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
          <Button variant="primary" onClick={handleClose}>
            Save Changes
          </Button>
        </Modal.Footer>
      </Modal>


    </>
  );
}

export default EditScheduler