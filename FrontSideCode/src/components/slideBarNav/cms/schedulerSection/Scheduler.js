import axios from "axios";
import React, { useEffect, useState } from "react";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import SchedulerTable from "./SchedulerTable";
import { API_BASE_URL, CLIENTLIST, SCHEDULER, TENANT_NAMESPACES } from "../../../constant-API/constants";


function Scheduler() {
  const [schedulerClient, setSchedulerClient] = useState([]);
  const [schedulerNamespace, setschedulerNamespace] = useState([]);

  const [scheduleClientId, setScheduleClientId] = useState('');
  const [scheduleNamespaceId, setscheduleNamespaceId] = useState('');
  const [scheduleType, setScheduleType] = useState('');
  const [scheduleTime, setScheduleTime] = useState('');
  const [scheduleWeekly, setScheduleWeekly] = useState('');
  const [scheduleMonthly, setScheduleMonthly] = useState('');
  const [refreshTable, setRefreshTable] = useState(false);
  const [errorMessage, setErrorMessage] = useState('')

  const handleScheduleClientId = (e) => {
    setScheduleClientId(e.target.value)
  }

  const handleScheduleNameSpace = (e) => {
    setscheduleNamespaceId(e.target.value)
  }

  const handleMonthNumber = (e) => {
    const getMonthNumber = e.target.value
    if (/^[1-9]\d*$/.test(getMonthNumber) && getMonthNumber <= 31) {
      setScheduleMonthly(getMonthNumber);
      setErrorMessage('')
    } else {
      setScheduleMonthly(getMonthNumber.slice(1, 31))

      setTimeout(() => {
        setErrorMessage('Please Choose 1 to 31 Number Only');
      }, 2000);
    }
  }

  //Get the Token
  const header = {
    Authorization: localStorage.getItem("token"),
  };

  //get the role of the user
  const getRoleUser = localStorage.getItem("roles");
  const isSuperAdmin = ["SUPERADMIN"].includes(getRoleUser);

  //Get All Client API
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(
          `${API_BASE_URL}${CLIENTLIST}`,
          { headers: header }
        );
        if (Array.isArray(response.data)) {
          if (response.data.length > 0 && response.data[0].clientId) {
            setSchedulerClient(response.data);
          } else {
            // console.log("Error", response.data);
          }
        } else {
          // console.error("Response data is not an array:", response.data);
        }
      } catch (error) {
        console.log("Error fetching client data", error);
      }
    };
    fetchData();
  }, []);

  // get NameSpace
  useEffect(() => {
    const nameSpaceFetchingData = async () => {
      try {
        const response = await axios.get(
          `${API_BASE_URL}${TENANT_NAMESPACES}`,
          {
            headers: header,
          }
        );
        setschedulerNamespace(response.data);
      } catch (error) {
        console.log("Error Fetching NameSpace Data ", error);
      }
    };
    nameSpaceFetchingData();
  }, []);

  // Post scheduler Data
  const handleSubmitScheduler = async (e) => {
    e.preventDefault();

    // if (!scheduleClientId || !scheduleNamespaceId || !scheduleWeekly || !scheduleTime || !scheduleType) {
    //   toast.error('Please select all fields.', { position: toast.POSITION.TOP_RIGHT });
    //   return;
    // }
   

    let postData = {
      clientId: scheduleClientId,
      namespaceId: scheduleNamespaceId,
      scheduleType,
      scheduleTime,
    };

    if (scheduleType === 'Weekly') {
      postData.weeklyScheduleDay = scheduleWeekly;
    } else if (scheduleType === 'Monthly') {
      postData.monthlyScheduleDate = scheduleMonthly;
    }

    try {
      const res = await axios.post(
        `${API_BASE_URL}${SCHEDULER}`,
        postData,
        {
          headers: header
        }
      );

      setScheduleClientId("");
      setscheduleNamespaceId("");
      setScheduleType("");
      setScheduleTime("");
      setScheduleMonthly("");
      setScheduleWeekly("");
      // Show success message
      setRefreshTable(!refreshTable);
      toast.success('Successfully Saved', { position: toast.POSITION.TOP_RIGHT });
    } catch (error) {
      toast.error('Error: ' + error.response?.data?.message || 'An error occurred', { position: toast.POSITION.TOP_RIGHT });
    }
  };


  return (
    <div>
      <ToastContainer />
      <div className="" id="schedulerSection">
        <h4 className="mx-3">Scheduler Section</h4>
        <form onSubmit={handleSubmitScheduler}>
          <div className="row d-flex justify-content-between">
            {isSuperAdmin && (
              <>
                <div className="col-md-6 col-lg-6 col-6">
                  <label htmlFor="">Client Name</label>
                  <select class="form-select" name="scheduleClientId" value={scheduleClientId} onChange={handleScheduleClientId} required>
                    <option selected>Select Client Name</option>
                    {schedulerClient.map((clientname, id) => {
                      return (
                        <>
                          <option key={id} value={clientname.clientId}>
                            {clientname.spocName}
                          </option>
                        </>
                      );
                    })}
                  </select>
                </div>
              </>
            )}


            <div className={`${isSuperAdmin ? 'col-6' : 'col-md-12 col-lg-12 col-sm-12'}`}>
              <label htmlFor="">Namespace</label>
              <select name="scheduleNamespaceId" value={scheduleNamespaceId} onChange={handleScheduleNameSpace} class="form-select" aria-label="Default select example" required>
                <option selected>Select Namespace</option>
                {schedulerNamespace.map((nameSpace, id) => (
                  <>
                    <option key={id} value={nameSpace.namespaceId}>
                      {nameSpace.namespaceName}
                    </option>
                  </>
                ))}
              </select>
            </div>
          </div>


          <div className="row d-flex justify-content-center">
            <div className="col-12 col-md-4">
              <label htmlFor="">Schedule Type</label>
              <select name="schedulerType"
                onChange={(e) => { setScheduleType(e.target.value) }}
                value={scheduleType} class="form-select" required>
                <option selected>Select Schedule Type</option>
                <option value="Daily">Daily</option>
                <option value="Weekly">Weekly</option>
                <option value="Monthly">Monthly</option>
              </select>
            </div>

            {scheduleType === "Daily" ? (
              <>
                <div className="col-12  col-md-4">
                  <label htmlFor="">Schedule Time</label>
                  <input type="time" name="schedulerTime" className="form-control"
                    value={scheduleTime} onChange={(e) => { setScheduleTime(e.target.value) }} placeholder="HH:MM:SS" step="1" />
                </div>
              </>
            ) : scheduleType === "Weekly" ? (
              <>
                <div className="col-12 col-md-4">
                  <label htmlFor="">Schedule Time</label>
                  <input type="time" name="schedulerTime" className="form-control" value={scheduleTime} onChange={(e) => {setScheduleTime(e.target.value) }} placeholder="HH:MM:SS" step="1" />
                </div>

                <div className="col-12 col-md-4">
                  <label htmlFor="">Weekly Schedule Day</label>
                  <select name="schedulerWeekly" class="form-select" onChange={(e) => {setScheduleWeekly(e.target.value) }} value={scheduleWeekly}>
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

              </>
            ) : scheduleType === "Monthly" ? (
              <>
                <div className="col-12 col-md-4">
                  <label htmlFor="">Schedule Time</label>
                  <input type="time" name="schedulerTime" className="form-control" value={scheduleTime} onChange={(e) => {setScheduleTime(e.target.value) }} placeholder="HH:MM:SS" step="1" required />
                </div>

                <div className="col-12 col-md-4">
                  <label htmlFor="">Monthly Schedule Date</label>
                  <input type="number" placeholder="Enter Date Number" onChange={handleMonthNumber} value={scheduleMonthly} name="schedulerMonthly" className="form-control" />
                  {errorMessage && <div className="text-danger">Please Choose 1 to 31 number only</div>}
                </div>
              </>
            )
              : (
                <>


                </>
              )}
          </div>

          <div className="d-flex justify-content-center mt-3">
            <button className="btn" type="submit" id="UserModuleBtn">Submit </button>
          </div>
        </form>
      </div>

      <SchedulerTable refreshTable={refreshTable} />
    </div>

    // scheduler Table

  );
}

export default Scheduler;
