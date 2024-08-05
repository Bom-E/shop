import React from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import UserHeader from './components/user/UserHeader';
import Navigation from './components/common/Navigation';
import Home from './pages/Home';
import Domestic from './pages/Domestic';
import Foreign from './pages/Foreign';
import Goods from './pages/Goods';
import Events from './pages/Events';

const navItems = [
    {path : '/', label : 'Home', component : Home}
    , {path : '/domestic', label : '국내도서', component : Domestic}
    , {path : '/foreign' , label : '해외도서', component : Foreign}
    , {path : '/goods', label : '굿즈', component : Goods}
    , {path : '/events', label : '이벤트', component : Events}
]

function Header() {
  const navigate= useNavigate();
  const location = useLocation();
  
  const isActive = (path) => location.pathname === path;
  const navigateTo = (path) => navigate(path);


  return(
    <header className="bg-yellow-100">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row items-center justify-between py-4">
          <UserHeader navHome={() => navigateTo('/')}/>
          
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
      <Navigation navItems={navItems} isActive={isActive} navigateTo={navigateTo} className="bg-white shadow"/>
      <main className="flex-grow container mx-auto px-4 py-8">
        <Routes>
        {navItems.map(({path, component : Component}) => (
          <Route key={path} path={path} element={<Component/>}/>
        ))}
        </Routes>
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
