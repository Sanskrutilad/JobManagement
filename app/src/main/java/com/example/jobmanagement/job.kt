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
//    location: String,
//    salary: Number,
//});
//
//const Job = mongoose.model('Job', jobSchema);
//
//// Routes
//app.post('/api/jobs', async (req, res) => {
//    const newJob = new Job(req.body);
//    try {
//        await newJob.save();
//        res.status(201).json(newJob);
//    } catch (err) {
//        res.status(400).json({ error: err.message });
//    }
//});
//
//app.get('/api/jobs', async (req, res) => {
//    try {
//        const jobs = await Job.find();
//        res.json(jobs);
//    } catch (err) {
//        res.status(500).json({ error: err.message });
//    }
//});
//
//app.put('/api/jobs/:id', async (req, res) => {
//    try {
//        const updatedJob = await Job.findByIdAndUpdate(req.params.id, req.body, { new: true });
//        res.json(updatedJob);
//    } catch (err) {
//        res.status(400).json({ error: err.message });
//    }
//});
//
//app.delete('/api/jobs/:id', async (req, res) => {
//    try {
//        await Job.findByIdAndDelete(req.params.id);
//        res.json({ message: 'Job deleted successfully!' });
//    } catch (err) {
//        res.status(500).json({ error: err.message });
//    }
//});
//
//// Start the server
//app.listen(PORT, () => {
//    console.log(`Server running on http://localhost:${PORT}`);
//});
//
//
