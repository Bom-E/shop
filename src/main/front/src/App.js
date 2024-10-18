import React from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import UserHeader from './components/user/UserHeader';
import UserFooter from './components/user/UserFooter';
import Navigation from './components/common/Navigation';
import routes, { navItems } from './routes';

function Header() {

  const navigate= useNavigate();

  const navigateTo = (path) => navigate(path);

  return(

    <div>
    
        <header className="bg-transparent">
          <div className="container mx-auto px-4">
            <div className="flex flex-col md:flex-row items-center justify-between">
              <UserHeader navHome={() => navigateTo('/')} userSign={() => navigateTo('/auth/sign1')} login={() => navigateTo('/auth/login')}/>
              
            </div>
          </div>

        </header>

    </div>

  );

}

function AppContent() {
  const navigate= useNavigate();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;
  const navigateTo = (path) => navigate(path);

  const hideHederFooter = location.pathname.includes('auth');

  return (
    
    <div className="flex flex-col min-h-screen">
    {!hideHederFooter && (
      <>
        <Header/>
          <div className="xl:mt-8">
            <Navigation navItems={navItems} isActive={isActive} navigateTo={navigateTo} className="bg-white shadow"/>
          </div>
      </>
    )}
        
        
      <main className="flex-grow container mx-auto px-4 py-8 overflow-auto">
        <div>
          <Routes>
            {routes.map((route, index) => (
              <Route 
                key={index}
                path={route.path}
                element={route.element || <route.component />}>
                {route.children && route.children.map((childRoute, childIndex) => (
                  <Route
                    key={childIndex}
                    index={childRoute.index}
                    path={childRoute.path}
                    element={childRoute.element || <childRoute.component />}/>
                ))}
              </Route>
            ))}
          </Routes>
        </div>  
      </main>

      {!hideHederFooter && <UserFooter/>}
      
    </div>      
  );
}

function App(){
  return(
    <Router>
      <AppContent />
    </Router>
  );
}

export default App;
