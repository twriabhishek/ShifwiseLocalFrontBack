import React from 'react';
// import jsPDF from 'jspdf';
// import 'jspdf-autotable';

function Download({ tableList,tableData }) {
    const downloadPDF=()=>{
        console.log(tableList);
        console.log(tableData);
    }
    
    return (
        <div className="dropdown">
            <button className="btn btn-danger dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                EXPORT
            </button>
            <ul className="dropdown-menu">
                <li>
                    <a className="dropdown-item" href="#" onClick={downloadPDF}>
                        PDF
                    </a>
                </li>
                {/* Add handlers for Excel and CSV as needed */}
                <li><a className="dropdown-item" href="#">EXCEL</a></li>
                <li><a className="dropdown-item" href="#">CSV</a></li>
            </ul>
        </div>
    );
}

export default Download;
