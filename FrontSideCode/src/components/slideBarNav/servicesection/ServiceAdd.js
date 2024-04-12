import React from 'react'

function ServiceAdd() {
    return (
        <>
            <div className="container px-4">
                <div id="serviceSection">
                    <h4 className='mt-3'>Service Section</h4>
                    <div className="row">
                        <div className="col-6">
                            <label htmlFor="">Enter URL </label>
                            <input type="text" placeholder='Add URL Here' className='form-control' name='url' />
                        </div>

                        <div className="col-6">
                        <label htmlFor="">Enter Serivce Name  </label>
                            <input type="text" placeholder='Service Name' className='form-control' name='servicename' />
                        </div>
                    </div>

                    <div className="row justify-align-content-center">
                        <div className="col-6">
                        <label htmlFor="">Enter Serivce Description  </label>
                            <input type="text" placeholder='Service Description' className='form-control' name='servicedescription' />
                        </div>

                        <div className="col-6">
                        <label htmlFor="">Enter Stage  </label>
                            <input type="text" placeholder='Enter Your Stage' className='form-control' name='servicestage' />
                        </div>
                    </div>

                </div>
                <div className='d-flex justify-content-center mt-3' >
                    <button className='' id='ServiceButton'>Submit</button>
                </div>
            </div>
        </>
    )
}

export default ServiceAdd