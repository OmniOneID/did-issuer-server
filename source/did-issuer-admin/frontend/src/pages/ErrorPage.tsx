import React from 'react';
import { Typography, Button, Box } from '@mui/material';
import { useLocation } from 'react-router';

const ErrorPage: React.FC = () => {
  const location = useLocation();

  const goBackAndRefresh = () => {
    if (window.history.length > 1) {
      window.history.back();
    } else {
      window.location.href = "/";
    }
  };

  const errorMessage = location.state?.message || "An unexpected error occurred. Please try again later.";
  
  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      height="100vh"
      textAlign="center"
    >
      <Typography variant="h4" color="error" gutterBottom>
        Error
      </Typography>
      <Typography variant="body1" gutterBottom>
      {errorMessage}
      </Typography>
      <Button variant="contained" color="primary" onClick={goBackAndRefresh}>
        Go Back
      </Button>
    </Box>
  );
};

export default ErrorPage;
