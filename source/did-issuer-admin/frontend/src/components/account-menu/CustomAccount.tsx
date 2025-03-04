import React, { useState, useContext } from 'react';
import { IconButton, Popover, Box, Typography, Button } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';
import { AuthenticationContext, SessionContext } from '@toolpad/core/AppProvider';

const AccountMenu = () => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const authentication = useContext(AuthenticationContext);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    authentication?.signOut();
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);

  return (
    <>
      <IconButton onClick={handleClick} color="inherit">
        <SettingsIcon />
      </IconButton>
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
      >
        <Box sx={{ p: 2, minWidth: 200 }}>
          <Button fullWidth variant="outlined" sx={{ mt: 1 }} onClick={handleClose}>
            Logout
          </Button>
        </Box>
      </Popover>
    </>
  );
};

export default AccountMenu;
