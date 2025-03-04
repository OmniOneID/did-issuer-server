import { Button, Stack } from '@mui/material';
import { useDialogs } from '@toolpad/core/useDialogs';
import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router';
import CustomDialog from '../../components/dialog/CustomDialog';
import FullscreenLoader from '../../components/loading/FullscreenLoader';
import { useServerStatus } from '../../context/ServerStatusContext';
import { postData } from '../../utils/api';

const IssuerRegisterPage = () => {
  const navigate = useNavigate();
  const { setServerStatus, setIssuerInfo, serverStatus } = useServerStatus();
  const [isError, setIsError] = useState<boolean>(false);
  const dialogs = useDialogs();
  const [isLoading, setIsLoading] = useState(false);

  const API_BASE_URL = "/issuer/admin/v1";

  if (serverStatus === 'ACTIVATE') {
    return <Navigate to="/issuer-management" replace />;
  }

  return (
    <>
        <FullscreenLoader open={isLoading} />
        Issuer Register Page
    </>

  );
};

export default IssuerRegisterPage;
