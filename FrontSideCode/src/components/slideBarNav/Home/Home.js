// import React, { useEffect, useState } from "react";
// import AlertPopUp from "../userModule/createUser/AlertPopUp";
// import ConfirmationBox from "../userModule/createUser/ConfirmationBox";

// function Home() {
//   const [businessUnit, setBusinessUnit] = useState(null);
//   const [showPopup, setShowPopup] = useState(true);

//   useEffect(() => {
//     const businessUnit = localStorage.getItem("businessUnit");
//     const delay = 5000;

//     // If businessUnit is null, show the popup after the delay
//     if (businessUnit === null) {
//       const timer = setTimeout(() => {
//         console.log("Timeout executed. Setting showPopup to true");
//         // Use the current state value to avoid race conditions
//         setShowPopup(true);
//       }, 5000);

//       // Clear the timer on component unmount or when businessUnit is updated
//       return () => clearTimeout(timer);
//     }
//   }, []);

//   const handleClosePopup = () => {
//     setShowPopup(false);
//     console.log("Closing popup");
//   };
//   return (
//     <>
//       <div id="HomePage">
//         <img src="../img/HomePage.png" className="homepageImg" alt="homepage" />
//         <AlertPopUp showPopup={showPopup} handleClosePopup={handleClosePopup} />
//         <ConfirmationBox />
//       </div>
//     </>
//   );
// }

// export default Home;


































import React, { useEffect, useState } from "react";
import AlertPopUp from "../userModule/createUser/AlertPopUp";
import ConfirmationBox from "../userModule/createUser/ConfirmationBox";

function Home() {
  const [businessUnit, setBusinessUnit] = useState(null);
  const [showPopup, setShowPopup] = useState(true);

  useEffect(() => {
    const storedBusinessUnit = localStorage.getItem("businessUnit");

    // If businessUnit is null, show the popup
    if (storedBusinessUnit === 'null') {
      const timer = setTimeout(() => {
        setShowPopup(true);
      }, 5000);

      // Clear the timer on component unmount or when businessUnit is updated
      return () => clearTimeout(timer);
    } else {
      // If businessUnit is present, update the state to not show the popup
      setShowPopup(false);
    }
  }, []); // Empty dependency array to run the effect only once on mount

  const handleClosePopup = () => {
    setShowPopup(false);
  };

  return (
    <>
      <div id="HomePage">
        <img src="../img/HomePage.png" className="homepageImg" alt="homepage" />
        {showPopup && <AlertPopUp showPopup={showPopup} handleClosePopup={handleClosePopup} />}
        <ConfirmationBox />
      </div>
    </>
  );
}

export default Home;