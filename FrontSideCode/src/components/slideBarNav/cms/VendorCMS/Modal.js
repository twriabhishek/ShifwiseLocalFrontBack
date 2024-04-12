import React, { useState, useEffect } from 'react';

const VendorPopup = () => {
  const [systemNames, setSystemNames] = useState([]);
  const [selectedSystemName, setSelectedSystemName] = useState('');
  const [vendorName, setVendorName] = useState('');
  const [template, setTemplate] = useState('');
  const [error, setError] = useState(null);

  useEffect(() => {
    // Fetch system names from the API
    fetch('http://shiftwiecmsloadbalancer-b6d5c5d19ec0d36c.elb.us-east-1.amazonaws.com:8081/system')
      .then((response) => response.json())
      .then((data) => {
        console.log('API Response:', data); // Log the response
        if (data && data.systemNames && Array.isArray(data.systemNames)) {
          setSystemNames(data.systemNames);
        } else {
          setError('Invalid system names data');
        }
      })
      .catch((error) => {
        setError('Error fetching system names');
        console.error('Error fetching system names:', error);
      });
  }, []);
  

  const handleSubmit = (e) => {
    e.preventDefault();

    // Create an object with the form data
    const formData = {
      systemName: selectedSystemName,
      vendorName,
      template,
    };

    // Send the data to the API
    fetch('http://shiftwiecmsloadbalancer-b6d5c5d19ec0d36c.elb.us-east-1.amazonaws.com:8081/vendor', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData),
    })
      .then((response) => {
        if (response.ok) {
          // Handle success here (e.g., show a success message)
        } else {
          setError('Error submitting data');
          console.error('Error submitting data:', response.statusText);
        }
      })
      .catch((error) => {
        setError('Error submitting data');
        console.error('Error submitting data:', error);
      });
  };

  return (
    <div className="popup">
      <form onSubmit={handleSubmit}>
        {error && <p className="error">{error}</p>}
        <div className="form-group">
          <label htmlFor="systemName">Select System Name</label>
          <select
            id="systemName"
            name="systemName"
            value={selectedSystemName}
            onChange={(e) => setSelectedSystemName(e.target.value)}
            required
          >
            <option value="">Select a System Name</option>
            {systemNames.map((name) => (
              <option key={name} value={name}>
                {name}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="vendorName">Vendor Name</label>
          <input
            type="text"
            id="vendorName"
            name="vendorName"
            value={vendorName}
            onChange={(e) => setVendorName(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="template">Template</label>
          <input
            type="text"
            id="template"
            name="template"
            value={template}
            onChange={(e) => setTemplate(e.target.value)}
            required
          />
        </div>

        <button type="submit">Submit</button>
      </form>
    </div>
  );
};

export default VendorPopup;
