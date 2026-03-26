import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import DashboardLayout from './layouts/DashboardLayout';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import Users from './pages/Users';
import Firms from './pages/Firms';
import B2BMatches from './pages/B2BMatches';
import Marketplaces from './pages/Marketplaces';
import Educations from './pages/Educations';
import Roles from './pages/Roles';
import ProtectedRoute from './components/ProtectedRoute';

// Example of other pages
const PlaceholderPage = ({ name }) => (
    <div style={{ padding: 20 }}>
        <h1>{name}</h1>
        <p>This is the {name.toLowerCase()} management page.</p>
    </div>
);

function App() {
    return (
        <Router>
            <Routes>
                {/* Public Routes */}
                <Route path="/login" element={<Login />} />

                {/* Protected Admin Routes */}
                <Route
                    path="/"
                    element={
                        <ProtectedRoute>
                            <DashboardLayout>
                                <Dashboard />
                            </DashboardLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/firms"
                    element={
                        <ProtectedRoute>
                            <DashboardLayout>
                                <Firms />
                            </DashboardLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/b2b-matches"
                    element={
                        <ProtectedRoute>
                            <DashboardLayout>
                                <B2BMatches />
                            </DashboardLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/educations"
                    element={
                        <ProtectedRoute>
                            <DashboardLayout>
                                <Educations />
                            </DashboardLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/marketplaces"
                    element={
                        <ProtectedRoute>
                            <DashboardLayout>
                                <Marketplaces />
                            </DashboardLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/users"
                    element={
                        <ProtectedRoute>
                            <DashboardLayout>
                                <Users />
                            </DashboardLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/roles"
                    element={
                        <ProtectedRoute>
                            <DashboardLayout>
                                <Roles />
                            </DashboardLayout>
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/settings"
                    element={
                        <ProtectedRoute>
                            <DashboardLayout>
                                <PlaceholderPage name="Settings" />
                            </DashboardLayout>
                        </ProtectedRoute>
                    }
                />
            </Routes>
        </Router>
    );
}

export default App;
