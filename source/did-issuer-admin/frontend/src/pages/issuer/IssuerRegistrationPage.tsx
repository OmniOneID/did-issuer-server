import { useDialogs } from '@toolpad/core/useDialogs';
import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router';
import FullscreenLoader from '../../components/loading/FullscreenLoader';

const IssuerRegisterPage = () => {
  const [isLoading, setIsLoading] = useState(false);

  const API_BASE_URL = "/issuer/admin/v1";

  return (
    <>
        <FullscreenLoader open={isLoading} />
        Issuer Register Page
    </>

  );
};

export default IssuerRegisterPage;
