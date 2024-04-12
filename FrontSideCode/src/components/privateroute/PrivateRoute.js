import { useEffect, useState } from "react";
import { Navigate, useLocation, useNavigate } from "react-router-dom";

export const AuthorizedUser = ({ children, fallbackUI }) => {
  const token = localStorage.getItem("token");
  const [redirectToLogin, setRedirectToLogin] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    let timeoutId;

    if (!token) {
      timeoutId = setTimeout(() => {
        setRedirectToLogin(true);
      }, 2000);
    }

    return () => {
      clearTimeout(timeoutId);
    };
  }, [token]);

  if (redirectToLogin) {
    return <Navigate to="/shiftwise" replace={true} />;
  }

  if (!token) {
    return fallbackUI || <p>You are not authorized. Please log in.</p>;
  }

  // If there is a token, render the children
  return children;
};
