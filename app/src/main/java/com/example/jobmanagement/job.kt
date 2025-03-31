//const express = require('express');
//const mongoose = require('mongoose');
//
//const app = express();
//app.use(express.json());
//
//const PORT = 5000;
//const MONGO_URI = 'mongodb+srv://ladsanskruti:Ghanashri*19@cluster0.gsyfl.mongodb.net/test?retryWrites=true&w=majority&appName=Cluster0';
//
//// Connect to MongoDB
//mongoose.connect(MONGO_URI, {
//    useNewUrlParser: true,
//    useUnifiedTopology: true,
//})
//.then(() => console.log('Connected to MongoDB'))
//.catch((err) => console.error('Error connecting to MongoDB:', err));
//
//// Job Schema and Model
//const jobSchema = new mongoose.Schema({
//    title: String,
//    description: String,
//    company: String,
//    companyId: { type: String, required: true }, // ✅ Store company UID as a String
//    location: String,
//    salary: Number,
//});
//
//const Job = mongoose.model('Job', jobSchema);
//
//// Company Schema and Model
//const companySchema = new mongoose.Schema({
//    uid: { type: String, required: true, unique: true }, // Firebase UID for authentication
//    companyName: { type: String, required: true },
//    industry: { type: String, required: true },
//    location: { type: String, required: true },
//    foundedYear: { type: Number, required: true },
//    email: { type: String, required: true, unique: true },
//    phone: { type: String, required: true },
//    size: { type: Number, required: true }, // Number of employees
//    revenue: { type: String }, // Approximate revenue (optional)
//    companyType: { type: String, required: true }, // Private, Public, etc.
//    jobsPosted: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Job' }] // ✅ Store job IDs as ObjectId
//});
//
//const Company = mongoose.model('Company', companySchema);
//
//// Create a Job
//app.post('/api/jobs', async (req, res) => {
//    let { title, description, company, companyId, location, salary } = req.body;
//
//    if (!companyId) {
//        return res.status(400).json({ error: 'companyId is required' });
//    }
//
//    try {
//        const newJob = new Job({ title, description, company, companyId, location, salary });
//        await newJob.save();
//
//        // Update the company's job list
//        await Company.findOneAndUpdate(
//                { uid: companyId }, // ✅ Find by UID
//        { $push: { jobsPosted: newJob._id } } // ✅ Store job ID as ObjectId
//        );
//
//        res.status(201).json(newJob);
//    } catch (err) {
//        res.status(400).json({ error: err.message });
//    }
//});
//
//// Get All Jobs (or Filter by Company ID)
//app.get('/api/jobs', async (req, res) => {
//    const { companyId } = req.query;
//    try {
//        let jobs = companyId ? await Job.find({ companyId }) : await Job.find();
//        res.json(jobs);
//    } catch (err) {
//        res.status(500).json({ error: err.message });
//    }
//});
//
//// Get a Specific Job
//app.get('/api/jobs/:id', async (req, res) => {
//    try {
//        const job = await Job.findById(req.params.id);
//        if (!job) return res.status(404).json({ message: 'Job not found' });
//        res.json(job);
//    } catch (err) {
//        res.status(500).json({ error: err.message });
//    }
//});
//
//// Update a Job
//app.put('/api/jobs/:id', async (req, res) => {
//    try {
//        const updatedJob = await Job.findByIdAndUpdate(req.params.id, req.body, { new: true });
//        res.json(updatedJob);
//    } catch (err) {
//        res.status(400).json({ error: err.message });
//    }
//});
//
//// Delete a Job
//app.delete('/api/jobs/:id', async (req, res) => {
//    try {
//        await Job.findByIdAndDelete(req.params.id);
//        res.json({ message: 'Job deleted successfully!' });
//    } catch (err) {
//        res.status(500).json({ error: err.message });
//    }
//});
//
//// Register a Company
//app.post('/api/companies/register', async (req, res) => {
//    try {
//        const existingCompany = await Company.findOne({ uid: req.body.uid });
//        if (existingCompany) return res.status(400).json({ error: 'Company with this UID already exists' });
//
//        const newCompany = new Company(req.body);
//        await newCompany.save();
//        res.status(201).json(newCompany);
//    } catch (err) {
//        res.status(400).json({ error: err.message });
//    }
//});
//
//// Get Company Details by UID
//app.get('/api/companies/:uid', async (req, res) => {
//    try {
//        const company = await Company.findOne({ uid: req.params.uid }).populate('jobsPosted');
//        if (!company) return res.status(404).json({ message: 'Company not found' });
//        res.json(company);
//    } catch (err) {
//        res.status(500).json({ error: err.message });
//    }
//});
//
//// Get Company Name by UID
//app.get('/api/companyName/:companyId', async (req, res) => {
//    try {
//        const company = await Company.findOne({ uid: req.params.companyId }, 'companyName');
//        if (!company) return res.status(404).json({ error: 'Company not found' });
//        res.json({ companyName: company.companyName });
//    } catch (err) {
//        res.status(500).json({ error: err.message });
//    }
//});
//
//
//const candidateSchema = new mongoose.Schema({
//    uid: { type: String, required: true, unique: true }, // Firebase UID for authentication
//    fullName: { type: String, required: true },
//    email: { type: String, required: true, unique: true },
//    phone: { type: String, required: true },
//    education: { type: String, required: true },
//    experience: { type: String },
//    skills: [String], // Array of skills
//});
//
//const Candidate = mongoose.model('Candidate', candidateSchema);
//
//app.post('/api/candidates/register', async (req, res) => {
//    try {
//        const existingCandidate = await Candidate.findOne({ uid: req.body.uid });
//        if (existingCandidate) {
//            return res.status(400).json({ error: 'Candidate with this UID already exists' });
//        }
//
//        const newCandidate = new Candidate(req.body);
//        await newCandidate.save();
//        res.status(201).json(newCandidate);
//    } catch (err) {
//        res.status(400).json({ error: err.message });
//    }
//});
//
//// Get Candidate Profile
//app.get('/api/candidates/:uid', async (req, res) => {
//    try {
//        const candidate = await Candidate.findOne({ uid: req.params.uid });
//        if (!candidate) {
//            return res.status(404).json({ message: 'Candidate not found' });
//        }
//        res.json(candidate);
//    } catch (err) {
//        res.status(500).json({ error: err.message });
//    }
//});
//
//// Start the server
//app.listen(PORT, () => {
//    console.log(`Server running on http://localhost:${PORT}`);
//});