import React from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import UserHeader from './components/user/UserHeader';
import Navigation from './components/common/Navigation';
import routes, { navItems } from './routes';

function Header() {
  const navigate= useNavigate();

  const navigateTo = (path) => navigate(path);

  return(
    <header className="bg-transparent">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row items-center justify-between">
          <UserHeader navHome={() => navigateTo('/')} userSign={() => navigateTo('/userSign/sign1')}/>
          
        </div>
      </div>

    </header>

  );

}

function AppContent() {
  const navigate= useNavigate();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;
  const navigateTo = (path) => navigate(path);


  return (
    <div className="flex flex-col min-h-screen">
      <Header/>
      <div className="xl:mt-8">
        <Navigation navItems={navItems} isActive={isActive} navigateTo={navigateTo} className="bg-white shadow"/>
      </div>
      
      <main className="flex-grow container mx-auto px-4 py-8 overflow-auto">
        <div>
          <Routes>
            {routes.map( route =>
              route.children ? (
                <Route key={route.path} path={route.path} element={<route.component/>}>
                  {route.children.map(childRoute => (
                    <Route key={childRoute.path || 'index'} index={childRoute.index} path={childRoute.path} element={childRoute.element || <childRoute.component/>}/>
                  ))}
                </Route>
              ) : (
                <Route key={route.path} path={route.path} element={<route.component/>}/>
              )
            )}
          </Routes>
        </div>
        
      </main>
      <footer className="bg-gray-200 py-4">
          <div className="container mx-auto px-4">

          </div>
      </footer>
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
