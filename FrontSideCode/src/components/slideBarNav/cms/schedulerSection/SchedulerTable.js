import React, { useEffect, useState } from 'react'
import EditTwoToneIcon from "@mui/icons-material/EditTwoTone";
import DeleteTwoToneIcon from "@mui/icons-material/DeleteTwoTone";
import { Button, Modal } from 'react-bootstrap';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import axios from 'axios';
import Download from '../../downloadTable/Download';
import { API_BASE_URL, SCHEDULER } from '../../../constant-API/constants';


function SchedulerTable({ refreshTable }) {
    const [tableList, setTableList] = useState([])
    const [show, setShow] = useState(false);
    const [editShow, setEditShow] = useState(false);
    const [selectedSchedulerId, setSelectedSchedulerId] = useState(null);
    const [selectedEditSchedulerId, setSelectedEditSchedulerId] = useState(null);
    const [RefreshTableDelete, setRefreshTableDelete] = useState(false);
    const [editStore, setEditStore] = useState();

    // Edit Section
    const [editSchedulerID, setEditSchedulerID] = useState(null);
    const [editClientId, setEditClientId] = useState('');
    const [editNamespaceId, setEditNamespaceId] = useState('');
    const [editScheduleType, setEditScheduleType] = useState('');
    const [editScheduleTime, setEditScheduleTime] = useState('');
    const [editScheduleWeekly, setEditScheduleWeekly] = useState('');
    const [editScheduleMonthly, setEditScheduleMonthly] = useState('');
    const [editErrorSchedulerMonthly, setEditErrorSchedulerMonthly] = useState('')

    //handle Client and NameSpace Id not Editable
    const [alertMessage, setAlertMessage] = useState('');
    const [alertNameSpaceMessage, setAlertNameSpaceMessage] = useState('');

    const handleAlertMessageNameSpace = () => {
        setAlertNameSpaceMessage('Namespace Id will not Edit')
    }
    const handleAlertMessage = () => {
        setAlertMessage('Client Id will not Edit')
    }

    const handleEditSchedulerMonthly = (e) => {
        const myValue = e.target.value;
        if (myValue >= 1 && myValue <= 31) {
            setEditScheduleMonthly(myValue)
        } else {
            setEditErrorSchedulerMonthly('Choose 1 to 31 Date Number')
        }
    }

    const handleClose = () => {
        setShow(false);
        setEditShow(false)
        setAlertMessage('');
        setAlertNameSpaceMessage('')
        setEditErrorSchedulerMonthly('')
    }

    //Delete Item
    const handleSchedulerDelete = (schedulerId) => {
        setShow(true);
        setSelectedSchedulerId(schedulerId);
    };

    //Edit item
    const handleSchedulerEdit = (schedulerId, scheduleClientId, namespaceId, schedulerType, scheduleTime, schedulerWeekly, schedulerMonthly) => {
        // console.log('my id', schedulerId);
        // console.log('Client Id', scheduleClientId);
        // console.log('NameSpaceId', namespaceId);
        // console.log('schedulerType', schedulerType);
        // console.log('Time', scheduleTime);
        // console.log('Weekly', schedulerWeekly);
        // console.log('Monthly', schedulerMonthly);

        setEditSchedulerID(schedulerId)
        setEditClientId(scheduleClientId);
        setEditNamespaceId(namespaceId);
        setEditScheduleType(schedulerType);
        setEditScheduleTime(scheduleTime);
        setEditScheduleWeekly(schedulerWeekly);
        setEditScheduleMonthly(schedulerMonthly);
        setEditShow(true);
        // console.log("Edit my selectedEditSchedulerId", editSchedulerID);
    }

    // Delete Data
    const handleDataDelete = () => {
        if (selectedSchedulerId) {
            axios
                .delete(`${API_BASE_URL}${SCHEDULER}/${selectedSchedulerId}`, {
                    headers: header,
                })
                .then((res) => {
                    const responseData = res.data;
                    if (Array.isArray(responseData)) {
                        setTableList(responseData);
                    } else {
                        console.error('Invalid response data after deletion:', responseData);
                    }
                    toast.success("Data Deleted Successfully", { position: toast.POSITION.TOP_RIGHT })
                    setRefreshTableDelete(!RefreshTableDelete);
                    handleClose();
                })
                .catch((error) => {
                    console.error('Error deleting data:', error);
                });
        }
    };

    //Get the Token
    const header = {
        Authorization: localStorage.getItem("token"),
    };

    useEffect(() => {
        try {
            axios.get(`${API_BASE_URL}${SCHEDULER}`, { headers: header }).then((res) => {
                setTableList(res.data)
            })
        } catch (error) {
            console.log(error);
        }
    }, [refreshTable, RefreshTableDelete])

    // handleEditSubmit
    const handleEditSubmit = () => {
        if (!editSchedulerID) {
            return;
        }
        const editSubmitData = {
            schedulerId: editSchedulerID,
            clientId: editClientId,
            namespaceId: editNamespaceId,
            scheduleType: editScheduleType,
            scheduleTime: editScheduleTime,

        };

        if (editScheduleType === 'Weekly') {
            editSubmitData.weeklyScheduleDay = editScheduleWeekly;
        } else if (editScheduleType === 'Monthly') {
            editSubmitData.monthlyScheduleDate = editScheduleMonthly;
        }

        axios.put(
            `${API_BASE_URL}${SCHEDULER}/${editSchedulerID}`,
            editSubmitData,
            { headers: header }
        )
            .then((res) => {
                //Comparing the Id 
                const updatedList = tableList.map(item => {
                    if (item.schedulerId === editSchedulerID) {
                        return res.data;
                    }
                    return item;
                });

                setTableList(updatedList);

                setEditSchedulerID("")
                setEditClientId("");
                setEditNamespaceId("");
                setEditScheduleType("");
                setEditScheduleTime("");
                setEditScheduleWeekly("");
                setEditScheduleMonthly("");
                toast.success('Data Update Sucessfully', { position: toast.POSITION.TOP_RIGHT });
                setEditShow(false)
            })
            .catch((error) => {
                if (error.response) {
                    console.error("Server responded with:", error.response.data);
                }
            });
    };

    return (
        <>
            <ToastContainer />
            <div id="schedulerTable">
                <div className="container">
                    <div className='d-flex justify-content-end mb-1'>
                        <Download tableList={tableList} />
                    </div>
                    <table className='schedulerTable'>
                        <thead>
                            <tr className='table-header'>
                                <th scope="col" className='head'>Client ID </th>
                                <th scope="col" className='head'>NameSpace ID</th>
                                <th scope="col" className='head'>Schedule Type</th>
                                <th scope="col" className='head'>Schedule Time</th>
                                <th scope="col" className='head'>Schedule Weekly</th>
                                <th scope="col" className='head'>Schedule Monthly (Date) </th>
                                <th scope="col" className='head'>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                tableList.map((item, index) => {
                                    return (
                                        <>
                                            <tr key={index}>
                                                <td>{item.clientId}</td>
                                                <td>{item.namespaceId}</td>
                                                <td>{item.scheduleType}</td>
                                                <td>{item.scheduleTime}</td>
                                                <td>{item.weeklyScheduleDay}</td>
                                                <td>{item.monthlyScheduleDate}</td>
                                                <td>
                                                    <div className="d-flex justify-content-center align-items-center">
                                                        <div className=" m-auto mx-1" >
                                                            <EditTwoToneIcon className="edit- text-center myEditIcons"
                                                                onClick={() => {
                                                                    handleSchedulerEdit(item.schedulerId, item.clientId, item.namespaceId,
                                                                        item.scheduleType, item.scheduleTime, item.weeklyScheduleDay, item.monthlyScheduleDate
                                                                    )
                                                                }}
                                                            />
                                                        </div>

                                                        <div className=" m-auto mx-1">
                                                            <DeleteTwoToneIcon className="edit- text-center myEditIcons"
                                                                onClick={() => { handleSchedulerDelete(item.schedulerId) }}
                                                            />
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>
                                        </>
                                    )
                                })
                            }

                        </tbody>
                    </table>
                </div>
            </div>


            {/* Modal Section is Here */}
            <>
                <Modal centered show={show} onHide={handleClose}>
                    <Modal.Header closeButton>
                        <Modal.Title>Delete Data</Modal.Title>
                    </Modal.Header>
                    <Modal.Body className='' style={{ fontSize: "16px" }}>You want to delete Data </Modal.Body>
                    <Modal.Footer className='d-flex justify-content-center'>
                        <Button id='UserModuleBtn' onClick={handleDataDelete}>
                            Delete
                        </Button>
                    </Modal.Footer>
                </Modal>
            </>

            {/* Edit Modal */}
            <>
                <Modal show={editShow} onHide={handleClose}>
                    <Modal.Header closeButton>
                        <Modal.Title>Edit Schedule</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <form>
                            <div className="row mb-2">
                                {editClientId && (
                                    <div className="col-6 col-md-6 col-lg-6">
                                        <label htmlFor="clientId" className="form-label">Client ID</label>
                                        <input type="text" className="form-control" onClick={handleAlertMessage} value={editClientId} name='clientId' id="clientId" readOnly />
                                        <small className='text-danger'>{alertMessage}</small>
                                    </div>
                                )}
                                {editNamespaceId && (
                                    <div className="col-5 col-md-6 col-lg-6">
                                        <label htmlFor="clientId" className="form-label">Namespace ID</label>
                                        <input type="text" onClick={handleAlertMessageNameSpace} className="form-control" id="" name='namespaceId' value={editNamespaceId} readOnly />
                                        <small className='text-danger'>{alertNameSpaceMessage}</small>
                                    </div>
                                )}
                            </div>

                            {editScheduleType && (
                                <div className="mb-2">
                                    <label htmlFor="namespaceId" className="form-label">Schedule Type</label>
                                    <select onChange={(e) => {
                                         setEditScheduleType(e.target.value)

                                    }} value={editScheduleType} name='editScheduleType' className="form-select" id="namespaceId" >
                                        {/* <option >Select Schedule Type</option> */}
                                        <option value="Daily">Daily</option>
                                        <option value="Weekly">Weekly</option>
                                        <option value="Monthly">Monthly</option>
                                    </select>
                                </div>
                            )}

                            {editScheduleTime && (
                                <div className="mb-2">
                                    <label htmlFor="namespaceId" className="form-label">ScheDule Time</label>
                                    <input type="time" onChange={(e) => {setEditScheduleTime(e.target.value) }} value={editScheduleTime} step="1" name='editScheduleTime' className="form-control" id="namespaceId" />
                                </div>
                            )}

                            {editScheduleType === "Weekly" && (
                                <div className="mb-2">
                                    <label htmlFor="namespaceId" className="form-label">Schedule Weekly</label>
                                    <select onChange={(e) => {setEditScheduleWeekly(e.target.value) }} value={editScheduleWeekly} name='editScheduleWeekly' className="form-select">
                                        <option selected>Select Weekly Schedule Day</option>
                                        <option value="Monday">Monday</option>
                                        <option value="Tuesday">Tuesday</option>
                                        <option value="Wednesday">Wednesday</option>
                                        <option value="Thurday">Thurday</option>
                                        <option value="Friday">Friday</option>
                                        <option value="Saturday">Saturday</option>
                                        <option value="Sunday">Sunday</option>
                                    </select>
                                </div>
                            )}

                            {editScheduleType === "Monthly" && (
                                <div className="mb-2">
                                    <label htmlFor="namespaceId" className="form-label">Schedule Monthly</label>
                                    <input type="number" onChange={(e) => { handleEditSchedulerMonthly(e) }} value={editScheduleMonthly} name='editScheduleMonthly' required className="form-control" id="namespaceId" />
                                    <small className='text-danger'>{editErrorSchedulerMonthly}</small>
                                </div>
                            )}

                        </form>
                    </Modal.Body>
                    <Modal.Footer className='d-flex justify-content-center'>
                        <Button id='UserModuleBtn' className='' onClick={handleEditSubmit}>
                            Save Changes
                        </Button>
                    </Modal.Footer>
                </Modal>
            </>
        </>
    )
}

export default SchedulerTable