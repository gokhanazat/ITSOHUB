import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { Box, CircularProgress } from '@mui/material';

const ProtectedRoute = ({ children }) => {
    const { user, role, loading } = useAuth();

    if (loading) {
        return (
            <Box sx={{ display: 'flex', height: '100vh', alignItems: 'center', justifyContent: 'center' }}>
                <CircularProgress />
            </Box>
        );
    }

    // Role check: Only allow if user exists and role is ADMIN or admin
    const userRole = (role || '').toUpperCase();
    if (!user || userRole !== 'ADMIN') {
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default ProtectedRoute;
