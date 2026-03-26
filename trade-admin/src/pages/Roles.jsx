import React from 'react';
import { Box, Typography, Paper, Alert, AlertTitle, Grid, Card, CardContent, Divider } from '@mui/material';
import { AdminPanelSettings as RoleIcon, Security as PermissionIcon, Lock as LockIcon } from '@mui/icons-material';

const Roles = () => {
    return (
        <Box sx={{ pb: 4 }}>
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" fontWeight="800" color="primary.main">
                    Rol ve Yetki Yönetimi
                </Typography>
                <Typography variant="body1" color="textSecondary">
                    Sistem rolleri ve detaylı erişim yetkilerini yapılandırın
                </Typography>
            </Box>

            <Alert severity="info" sx={{ mb: 4, borderRadius: 3 }}>
                <AlertTitle sx={{ fontWeight: 'bold' }}>Gelecek Özellik</AlertTitle>
                Granüler yetkilendirme sistemi (RBAC) planlama aşamasındadır. Yakında her rol için ayrı yetki tanımlamaları bu ekran üzerinden yapılabilecek.
            </Alert>

            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 4, borderRadius: 4, opacity: 0.7 }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                            <RoleIcon sx={{ mr: 2, color: 'primary.main' }} />
                            <Typography variant="h6" fontWeight="bold">Sistem Rolleri</Typography>
                        </Box>
                        <Divider sx={{ mb: 2 }} />
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                            <Card variant="outlined" sx={{ borderRadius: 2 }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <Typography fontWeight="bold">ADMIN</Typography>
                                        <LockIcon fontSize="small" color="disabled" />
                                    </Box>
                                    <Typography variant="body2" color="textSecondary">Tüm sistem özelliklerine tam erişim.</Typography>
                                </CardContent>
                            </Card>
                            <Card variant="outlined" sx={{ borderRadius: 2 }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <Typography fontWeight="bold">COMPANY</Typography>
                                        <LockIcon fontSize="small" color="disabled" />
                                    </Box>
                                    <Typography variant="body2" color="textSecondary">Sadece kendi firma işlemlerine erişim.</Typography>
                                </CardContent>
                            </Card>
                        </Box>
                    </Paper>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 4, borderRadius: 4, height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#F8F9FA' }}>
                        <Box sx={{ textAlign: 'center' }}>
                            <PermissionIcon sx={{ fontSize: 60, color: 'action.disabled', mb: 2 }} />
                            <Typography variant="h6" color="textSecondary">Yetki Matrisi Yakında</Typography>
                            <Typography variant="body2" color="text.disabled">Erişim kontrol listeleri (ACL) burada yönetilecek.</Typography>
                        </Box>
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Roles;
