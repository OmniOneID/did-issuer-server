import { useEffect } from 'react';
import { useNavigate } from 'react-router';

type Props = {}

const VcManagementPage = (props: Props) => {
  const navigate = useNavigate();

  useEffect(() => {
    navigate('vc-management/namespace-management');
  }, [navigate]);

  return null;
}

export default VcManagementPage